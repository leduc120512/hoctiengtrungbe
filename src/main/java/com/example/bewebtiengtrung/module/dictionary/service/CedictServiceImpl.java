package com.example.bewebtiengtrung.module.dictionary.service;

import com.example.bewebtiengtrung.module.vocabulary.entity.Word;
import com.example.bewebtiengtrung.module.vocabulary.repository.WordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Nạp CC-CEDICT từ {@code classpath:dictionary/cedict_ts.u8.gz} và giữ trong bộ nhớ ở dạng NÉN.
 *
 * <p>Vì máy chủ chỉ có 512 MB RAM, dữ liệu KHÔNG được giữ dưới dạng 125 nghìn object {@link CedictEntry}
 * với List định nghĩa riêng (≈ 900 nghìn String + List). Thay vào đó:
 * <ul>
 *   <li>các mảng song song theo chỉ số mục: giản thể, phồn thể (null khi trùng giản thể), pinyin dạng số,
 *       và các nghĩa nối bằng {@code '/'} trong MỘT chuỗi;</li>
 *   <li>chỉ mục là {@code Map<String, int[]>} trỏ tới chỉ số mục;</li>
 *   <li>{@link CedictEntry} chỉ được dựng cho các mục thực sự trả về (≤ 50 mỗi lần tra).</li>
 * </ul>
 * Nạp chạy ở luồng nền để cổng HTTP mở sớm; các phép tra chờ ở {@link #awaitLoaded()}.
 *
 * <p>Định dạng mỗi dòng: {@code Traditional Simplified [pin1 yin1] /def 1/def 2/}; dòng {@code #} là chú thích.
 * Nguồn: CC-CEDICT (MDBG), giấy phép CC BY-SA 4.0.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CedictServiceImpl implements CedictService {

    private static final String RESOURCE = "dictionary/cedict_ts.u8.gz";
    private static final Pattern LINE = Pattern.compile("^(\\S+) (\\S+) \\[([^\\]]+)\\] /(.+)/\\s*$");
    private static final Pattern PAREN = Pattern.compile("\\([^)]*\\)");
    private static final Pattern NON_LETTER = Pattern.compile("[^a-z]+");
    private static final Duration SYSTEM_WORDS_TTL = Duration.ofMinutes(10);
    private static final int[] EMPTY = new int[0];

    /** Nghĩa mở đầu bằng các cụm này chỉ là biến thể / họ / cách viết cũ — đẩy xuống cuối khi xếp hạng. */
    private static final String[] LOW_VALUE_PREFIXES = {
            "variant of", "surname", "old variant", "see ", "used in", "(archaic)", "abbr. for"
    };
    /** Nghĩa chứa các cụm này ở bất kỳ đâu cũng là mục hiếm/rút gọn (ví dụ 圕 = "contraction of 图书馆"). */
    private static final String[] LOW_VALUE_ANYWHERE = { "contraction of", "contracted form", "variant of", "archaic" };

    private final WordRepository wordRepository;

    // ---- kho nén: chỉ số i = một mục từ điển
    private String[] simplified = new String[0];
    private String[] traditional = new String[0];   // null ⇒ trùng giản thể
    private String[] numbered = new String[0];
    private String[] defsJoined = new String[0];     // "def1/def2/…"

    private Map<String, int[]> bySimplified = Map.of();
    private TreeMap<String, int[]> bySimplifiedSorted = new TreeMap<>();
    private Map<String, int[]> byTraditional = Map.of();
    private Map<String, int[]> byPinyinKey = Map.of();
    private Map<String, int[]> byEnglishWord = Map.of();
    private int size;

    private final CountDownLatch loaded = new CountDownLatch(1);
    private volatile RuntimeException loadFailure;

    /** Cache tập chữ giản thể có trong bảng words (để xếp hạng), làm mới mỗi 10 phút. */
    private volatile Set<String> systemWords = Set.of();
    private volatile Instant systemWordsLoadedAt = Instant.EPOCH;

    // ------------------------------------------------------------------ nạp

    @PostConstruct
    void startLoading() {
        Thread t = new Thread(() -> {
            try {
                load();
            } catch (RuntimeException e) {
                loadFailure = e;
                log.error("Nạp CC-CEDICT thất bại: {}", e.getMessage());
            } finally {
                loaded.countDown();
            }
        }, "cedict-loader");
        t.setDaemon(true);
        t.start();
    }

    /** Chờ từ điển nạp xong (tối đa 3 phút); ném lỗi nếu nạp thất bại. */
    void awaitLoaded() {
        try {
            if (!loaded.await(3, TimeUnit.MINUTES)) {
                throw new IllegalStateException("Từ điển CC-CEDICT chưa nạp xong, thử lại sau");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi chờ nạp từ điển", e);
        }
        if (loadFailure != null) {
            throw loadFailure;
        }
    }

    /** Nạp đồng bộ — test gọi trực tiếp; runtime gọi từ luồng nền. */
    void load() {
        long started = System.nanoTime();
        List<String> simp = new ArrayList<>(130_000);
        List<String> trad = new ArrayList<>(130_000);
        List<String> num = new ArrayList<>(130_000);
        List<String> defs = new ArrayList<>(130_000);
        Map<String, IntList> mSimp = new HashMap<>(160_000);
        Map<String, IntList> mTrad = new HashMap<>(160_000);
        Map<String, IntList> mPin = new HashMap<>(140_000);
        Map<String, IntList> mEng = new HashMap<>(60_000);

        try (GZIPInputStream gz = new GZIPInputStream(new ClassPathResource(RESOURCE).getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(gz, StandardCharsets.UTF_8), 1 << 16)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                Matcher m = LINE.matcher(line);
                if (!m.matches()) {
                    continue;
                }
                int i = simp.size();
                String s = m.group(2);
                String t = m.group(1);
                simp.add(s);
                trad.add(t.equals(s) ? null : t);
                num.add(m.group(3));
                String joined = m.group(4);
                defs.add(joined);
                mSimp.computeIfAbsent(s, k -> new IntList()).add(i);
                mTrad.computeIfAbsent(t, k -> new IntList()).add(i);
                mPin.computeIfAbsent(PinyinUtils.normalizeKey(m.group(3)), k -> new IntList()).add(i);
                indexEnglish(mEng, joined, i);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Không nạp được từ điển CC-CEDICT từ " + RESOURCE, e);
        }

        simplified = simp.toArray(new String[0]);
        traditional = trad.toArray(new String[0]);
        numbered = num.toArray(new String[0]);
        defsJoined = defs.toArray(new String[0]);
        bySimplified = freeze(mSimp);
        bySimplifiedSorted = new TreeMap<>(bySimplified);
        byTraditional = freeze(mTrad);
        byPinyinKey = freeze(mPin);
        byEnglishWord = freeze(mEng);
        size = simplified.length;
        loaded.countDown();
        log.info("Đã nạp CC-CEDICT: {} mục, {} khoá pinyin, {} từ tiếng Anh, {} ms",
                size, byPinyinKey.size(), byEnglishWord.size(), (System.nanoTime() - started) / 1_000_000);
    }

    private static Map<String, int[]> freeze(Map<String, IntList> src) {
        Map<String, int[]> out = new HashMap<>(src.size() * 4 / 3 + 1);
        for (Map.Entry<String, IntList> e : src.entrySet()) {
            out.put(e.getKey(), e.getValue().toArray());
        }
        return out;
    }

    /** Mỗi từ tiếng Anh (≥ 2 chữ cái, chữ thường, bỏ ngoặc và CL:) trong nghĩa trỏ tới chỉ số mục. */
    private static void indexEnglish(Map<String, IntList> eng, String joined, int index) {
        Set<String> seen = new HashSet<>();
        for (String definition : joined.split("/")) {
            if (definition.startsWith("CL:")) {
                continue;
            }
            String cleaned = PAREN.matcher(definition).replaceAll(" ").toLowerCase(Locale.ROOT);
            for (String token : NON_LETTER.split(cleaned)) {
                if (token.length() >= 2 && seen.add(token)) {
                    eng.computeIfAbsent(token, k -> new IntList()).add(index);
                }
            }
        }
    }

    // ------------------------------------------------------------------ dựng mục khi trả về

    private CedictEntry entry(int i) {
        String s = simplified[i];
        String t = traditional[i] == null ? s : traditional[i];
        return new CedictEntry(t, s, numbered[i], PinyinUtils.numberedToMarked(numbered[i], false),
                List.of(defsJoined[i].split("/")));
    }

    private List<CedictEntry> entries(int[] ids) {
        List<CedictEntry> out = new ArrayList<>(ids.length);
        for (int i : ids) {
            out.add(entry(i));
        }
        return out;
    }

    // ------------------------------------------------------------------ API

    @Override
    public List<CedictEntry> lookupSimplified(String simplified) {
        awaitLoaded();
        if (simplified == null || simplified.isBlank()) {
            return List.of();
        }
        return entries(bySimplified.getOrDefault(simplified.trim(), EMPTY));
    }

    @Override
    public List<CedictEntry> reverseLookup(String pinyin, int limit) {
        awaitLoaded();
        String key = PinyinUtils.normalizeKey(pinyin);
        if (key.isEmpty()) {
            return List.of();
        }
        int[] found = byPinyinKey.get(key);
        if (found == null) {
            return List.of();
        }
        Set<String> inSystem = systemWords();
        return entries(rank(found, inSystem, null, Math.max(1, limit)));
    }

    @Override
    public List<CedictEntry> search(String q, int limit) {
        awaitLoaded();
        if (q == null || q.isBlank()) {
            return List.of();
        }
        String query = q.trim();
        int max = Math.max(1, limit);
        Set<String> inSystem = systemWords();
        if (query.codePoints().anyMatch(PinyinUtils::isCjk)) {
            return entries(searchChinese(query, max, inSystem));
        }
        return entries(searchEnglish(query.toLowerCase(Locale.ROOT), max, inSystem));
    }

    private int[] searchChinese(String query, int max, Set<String> inSystem) {
        LinkedHashSet<Integer> out = new LinkedHashSet<>();
        for (int i : bySimplified.getOrDefault(query, EMPTY)) {
            out.add(i);
        }
        for (int i : byTraditional.getOrDefault(query, EMPTY)) {
            out.add(i);
        }
        for (Map.Entry<String, int[]> e : bySimplifiedSorted.tailMap(query, true).entrySet()) {
            if (!e.getKey().startsWith(query)) {
                break;
            }
            for (int i : e.getValue()) {
                out.add(i);
            }
            if (out.size() >= max * 4) {
                break;
            }
        }
        int[] ids = out.stream().mapToInt(Integer::intValue).toArray();
        return rank(ids, inSystem, null, max);
    }

    private int[] searchEnglish(String query, int max, Set<String> inSystem) {
        List<String> tokens = new ArrayList<>();
        for (String token : NON_LETTER.split(query)) {
            if (token.length() >= 2) {
                tokens.add(token);
            }
        }
        if (tokens.isEmpty()) {
            return EMPTY;
        }
        tokens.sort(Comparator.comparingInt(t -> byEnglishWord.getOrDefault(t, EMPTY).length));
        int[] candidates = byEnglishWord.get(tokens.get(0));
        if (candidates == null) {
            return EMPTY;
        }
        for (int k = 1; k < tokens.size(); k++) {
            int[] other = byEnglishWord.get(tokens.get(k));
            if (other == null) {
                return EMPTY;
            }
            candidates = intersectSorted(candidates, other);
            if (candidates.length == 0) {
                return EMPTY;
            }
        }
        return rank(candidates, inSystem, String.join(" ", tokens), max);
    }

    /** Giao hai mảng chỉ số đã tăng dần (chỉ số được thêm theo thứ tự đọc file nên luôn tăng). */
    private static int[] intersectSorted(int[] a, int[] b) {
        int[] out = new int[Math.min(a.length, b.length)];
        int i = 0, j = 0, n = 0;
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) {
                out[n++] = a[i];
                i++;
                j++;
            } else if (a[i] < b[j]) {
                i++;
            } else {
                j++;
            }
        }
        return Arrays.copyOf(out, n);
    }

    // ------------------------------------------------------------------ xếp hạng

    /**
     * Xếp hạng: (khớp nghĩa tiếng Anh nếu có cụm) → có trong hệ thống → không phải mục biến thể/hiếm →
     * chữ ngắn hơn. Trả tối đa {@code max} chỉ số.
     */
    private int[] rank(int[] ids, Set<String> inSystem, String phrase, int max) {
        Integer[] boxed = new Integer[ids.length];
        for (int k = 0; k < ids.length; k++) {
            boxed[k] = ids[k];
        }
        Comparator<Integer> cmp = Comparator.comparingInt((Integer i) -> phrase == null ? 0 : englishMatchRank(i, phrase))
                .thenComparingInt(i -> inSystem.contains(simplified[i]) ? 0 : 1)
                .thenComparingInt(this::lowValueRank)
                .thenComparingInt(i -> simplified[i].codePointCount(0, simplified[i].length()));
        Arrays.sort(boxed, cmp);
        int n = Math.min(max, boxed.length);
        int[] out = new int[n];
        for (int k = 0; k < n; k++) {
            out[k] = boxed[k];
        }
        return out;
    }

    /**
     * 0 = NGHĨA ĐẦU TIÊN bằng đúng cụm (nghĩa chính của từ); 1 = một nghĩa phụ bằng đúng cụm;
     * 2 = một nghĩa bắt đầu bằng cụm; 3 = chỉ chứa. Nhờ vậy 图书馆 ("library") xếp trên 库
     * ("warehouse/storehouse/(file) library") dù 库 ngắn hơn.
     */
    private int englishMatchRank(int i, String phrase) {
        int best = 5;
        int position = 0;
        for (String definition : defsJoined[i].split("/")) {
            if (definition.startsWith("CL:")) {
                continue;
            }
            String raw = definition.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
            String d = PAREN.matcher(raw).replaceAll(" ").trim().replaceAll("\\s+", " ");
            boolean exactRaw = raw.equals(phrase) || raw.equals("to " + phrase);
            boolean exactStripped = d.equals(phrase) || d.equals("to " + phrase);
            if (exactRaw) {
                // "library" nguyên văn ở nghĩa đầu = nghĩa chính, tổng quát nhất
                return position == 0 ? 0 : Math.min(best, 2);
            }
            if (exactStripped) {
                // "library (partition on computer hard disk)" — đúng từ nhưng có chú thích thu hẹp
                best = Math.min(best, position == 0 ? 1 : 3);
            } else if (d.startsWith(phrase) || d.startsWith("to " + phrase)) {
                best = Math.min(best, 4);
            }
            position++;
        }
        return best;
    }

    private int lowValueRank(int i) {
        String all = defsJoined[i].toLowerCase(Locale.ROOT);
        int slash = all.indexOf('/');
        String first = slash < 0 ? all : all.substring(0, slash);
        for (String prefix : LOW_VALUE_PREFIXES) {
            if (first.startsWith(prefix)) {
                return 1;
            }
        }
        for (String needle : LOW_VALUE_ANYWHERE) {
            if (all.contains(needle)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String suggestEnglish(CedictEntry entry) {
        if (entry == null) {
            return null;
        }
        // Bỏ nghĩa lóng/thô tục: đây là gợi ý cho ứng dụng học, không phải từ điển đầy đủ.
        String joined = entry.definitions().stream()
                .filter(d -> !d.startsWith("CL:"))
                .filter(d -> !isInappropriate(d))
                .map(d -> d.replaceAll("\\s*CL:[^/]*", "").trim())
                .filter(d -> !d.isEmpty())
                .limit(3)
                .collect(Collectors.joining("; "));
        if (joined.isEmpty()) {
            return null;
        }
        return joined.length() > 500 ? joined.substring(0, 500) : joined;
    }

    /** Nghĩa mang nhãn lóng / thô tục / tiếng chửi trong CC-CEDICT. */
    private static boolean isInappropriate(String definition) {
        String d = definition.toLowerCase(Locale.ROOT);
        return d.contains("(slang)") || d.contains("(vulgar)") || d.contains("(coarse)")
                || d.contains("(derog") || d.contains("(offensive)") || d.contains("sexual")
                || d.contains("(euphemism)") || d.contains("(expletive)");
    }

    @Override
    public int size() {
        awaitLoaded();
        return size;
    }

    /** Tập chữ giản thể đang có trong hệ thống, làm mới lười mỗi 10 phút. */
    private Set<String> systemWords() {
        Instant now = Instant.now();
        if (Duration.between(systemWordsLoadedAt, now).compareTo(SYSTEM_WORDS_TTL) > 0) {
            synchronized (this) {
                if (Duration.between(systemWordsLoadedAt, now).compareTo(SYSTEM_WORDS_TTL) > 0) {
                    try {
                        systemWords = wordRepository.findAll().stream()
                                .map(Word::getSimplified)
                                .collect(Collectors.toUnmodifiableSet());
                    } catch (RuntimeException e) {
                        log.warn("Không đọc được bảng words để xếp hạng từ điển: {}", e.getMessage());
                        systemWords = Set.of();
                    }
                    systemWordsLoadedAt = now;
                }
            }
        }
        return systemWords;
    }

    /** Danh sách int tăng trưởng, gọn hơn ArrayList&lt;Integer&gt; khi nạp. */
    private static final class IntList {
        private int[] data = new int[2];
        private int n;

        void add(int v) {
            if (n == data.length) {
                data = Arrays.copyOf(data, n * 2);
            }
            data[n++] = v;
        }

        int[] toArray() {
            return Arrays.copyOf(data, n);
        }
    }
}
