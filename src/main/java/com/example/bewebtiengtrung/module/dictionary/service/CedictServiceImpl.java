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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Nạp CC-CEDICT từ {@code classpath:dictionary/cedict_ts.u8.gz} lúc khởi động và giữ ba chỉ mục:
 * theo chữ giản thể, theo khoá pinyin, và theo từng từ tiếng Anh trong nghĩa.
 *
 * <p>Định dạng mỗi dòng: {@code Traditional Simplified [pin1 yin1] /def 1/def 2/}. Dòng bắt đầu
 * bằng {@code #} là chú thích. Khoảng 125 nghìn mục, nạp trong dưới 2 giây.
 *
 * <p>Nguồn: CC-CEDICT (MDBG), giấy phép CC BY-SA 4.0.
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

    /** Nghĩa mở đầu bằng các cụm này chỉ là biến thể / họ / cách viết cũ — đẩy xuống cuối khi xếp hạng. */
    private static final String[] LOW_VALUE_PREFIXES = {
            "variant of", "surname", "old variant", "see ", "used in", "(archaic)", "abbr. for"
    };

    private final WordRepository wordRepository;

    private Map<String, List<CedictEntry>> bySimplified = Map.of();
    private TreeMap<String, List<CedictEntry>> bySimplifiedSorted = new TreeMap<>();
    private Map<String, List<CedictEntry>> byTraditional = Map.of();
    private Map<String, List<CedictEntry>> byPinyinKey = Map.of();
    private Map<String, List<CedictEntry>> byEnglishWord = Map.of();
    private int size;

    /** Cache tập chữ giản thể có trong bảng words (để xếp hạng), làm mới mỗi 10 phút. */
    private volatile Set<String> systemWords = Set.of();
    private volatile Instant systemWordsLoadedAt = Instant.EPOCH;

    @PostConstruct
    void load() {
        long started = System.nanoTime();
        Map<String, List<CedictEntry>> simp = new HashMap<>(160_000);
        Map<String, List<CedictEntry>> trad = new HashMap<>(160_000);
        Map<String, List<CedictEntry>> pin = new HashMap<>(80_000);
        Map<String, List<CedictEntry>> eng = new HashMap<>(120_000);
        int count = 0;

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
                String traditional = m.group(1);
                String simplified = m.group(2);
                String numbered = m.group(3);
                List<String> definitions = List.of(m.group(4).split("/"));
                CedictEntry entry = new CedictEntry(traditional, simplified, numbered,
                        PinyinUtils.numberedToMarked(numbered, false), definitions);
                simp.computeIfAbsent(simplified, k -> new ArrayList<>(2)).add(entry);
                trad.computeIfAbsent(traditional, k -> new ArrayList<>(2)).add(entry);
                pin.computeIfAbsent(PinyinUtils.normalizeKey(numbered), k -> new ArrayList<>(4)).add(entry);
                indexEnglish(eng, entry);
                count++;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Không nạp được từ điển CC-CEDICT từ " + RESOURCE, e);
        }

        bySimplified = simp;
        bySimplifiedSorted = new TreeMap<>(simp);
        byTraditional = trad;
        byPinyinKey = pin;
        byEnglishWord = eng;
        size = count;
        log.info("Đã nạp CC-CEDICT: {} mục, {} khoá pinyin, {} từ tiếng Anh, {} ms",
                count, pin.size(), eng.size(), (System.nanoTime() - started) / 1_000_000);
    }

    /** Mỗi từ tiếng Anh (≥ 2 chữ cái, chữ thường, bỏ ngoặc và CL:) trong nghĩa trỏ tới mục. */
    private static void indexEnglish(Map<String, List<CedictEntry>> eng, CedictEntry entry) {
        Set<String> seen = new HashSet<>();
        for (String definition : entry.definitions()) {
            if (definition.startsWith("CL:")) {
                continue;
            }
            String cleaned = PAREN.matcher(definition).replaceAll(" ").toLowerCase(Locale.ROOT);
            for (String token : NON_LETTER.split(cleaned)) {
                if (token.length() >= 2 && seen.add(token)) {
                    eng.computeIfAbsent(token, k -> new ArrayList<>(4)).add(entry);
                }
            }
        }
    }

    // ------------------------------------------------------------------ API

    @Override
    public List<CedictEntry> lookupSimplified(String simplified) {
        if (simplified == null || simplified.isBlank()) {
            return List.of();
        }
        List<CedictEntry> found = bySimplified.get(simplified.trim());
        return found == null ? List.of() : Collections.unmodifiableList(found);
    }

    @Override
    public List<CedictEntry> reverseLookup(String pinyin, int limit) {
        String key = PinyinUtils.normalizeKey(pinyin);
        if (key.isEmpty()) {
            return List.of();
        }
        List<CedictEntry> found = byPinyinKey.get(key);
        if (found == null) {
            return List.of();
        }
        Set<String> inSystem = systemWords();
        return found.stream()
                .sorted(rankComparator(inSystem))
                .limit(Math.max(1, limit))
                .toList();
    }

    @Override
    public List<CedictEntry> search(String q, int limit) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        String query = q.trim();
        int max = Math.max(1, limit);
        Set<String> inSystem = systemWords();

        if (query.codePoints().anyMatch(PinyinUtils::isCjk)) {
            return searchChinese(query, max, inSystem);
        }
        return searchEnglish(query.toLowerCase(Locale.ROOT), max, inSystem);
    }

    private List<CedictEntry> searchChinese(String query, int max, Set<String> inSystem) {
        LinkedHashSet<CedictEntry> out = new LinkedHashSet<>();
        List<CedictEntry> exact = bySimplified.get(query);
        if (exact != null) {
            out.addAll(exact);
        }
        List<CedictEntry> exactTrad = byTraditional.get(query);
        if (exactTrad != null) {
            out.addAll(exactTrad);
        }
        // Tiền tố giản thể: duyệt subMap của TreeMap, dừng sớm khi đủ.
        for (Map.Entry<String, List<CedictEntry>> e : bySimplifiedSorted.tailMap(query, true).entrySet()) {
            if (!e.getKey().startsWith(query)) {
                break;
            }
            out.addAll(e.getValue());
            if (out.size() >= max * 4) {
                break;
            }
        }
        return out.stream().sorted(rankComparator(inSystem)).limit(max).toList();
    }

    private List<CedictEntry> searchEnglish(String query, int max, Set<String> inSystem) {
        List<String> tokens = new ArrayList<>();
        for (String token : NON_LETTER.split(query)) {
            if (token.length() >= 2) {
                tokens.add(token);
            }
        }
        if (tokens.isEmpty()) {
            return List.of();
        }
        // Giao của các tập theo từng từ; bắt đầu từ tập nhỏ nhất để rẻ.
        tokens.sort(Comparator.comparingInt(t -> byEnglishWord.getOrDefault(t, List.of()).size()));
        List<CedictEntry> base = byEnglishWord.get(tokens.get(0));
        if (base == null) {
            return List.of();
        }
        List<CedictEntry> candidates = base;
        for (int i = 1; i < tokens.size(); i++) {
            List<CedictEntry> other = byEnglishWord.get(tokens.get(i));
            if (other == null) {
                return List.of();
            }
            Set<CedictEntry> otherSet = new HashSet<>(other);
            candidates = candidates.stream().filter(otherSet::contains).toList();
            if (candidates.isEmpty()) {
                return List.of();
            }
        }
        final String phrase = String.join(" ", tokens);
        Comparator<CedictEntry> byMatch = Comparator.comparingInt(e -> englishMatchRank(e, phrase));
        return candidates.stream()
                .sorted(byMatch.thenComparing(rankComparator(inSystem)))
                .limit(max)
                .toList();
    }

    /** 0 = một nghĩa bằng đúng cụm; 1 = một nghĩa bắt đầu bằng cụm; 2 = chỉ chứa. */
    private static int englishMatchRank(CedictEntry entry, String phrase) {
        int best = 2;
        for (String definition : entry.definitions()) {
            String d = PAREN.matcher(definition).replaceAll(" ").toLowerCase(Locale.ROOT).trim()
                    .replaceAll("\\s+", " ");
            if (d.equals(phrase) || d.equals("to " + phrase)) {
                return 0;
            }
            if (d.startsWith(phrase) || d.startsWith("to " + phrase)) {
                best = Math.min(best, 1);
            }
        }
        return best;
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
        return size;
    }

    // ------------------------------------------------------------------ xếp hạng

    private Comparator<CedictEntry> rankComparator(Set<String> inSystem) {
        return Comparator
                .comparingInt((CedictEntry e) -> inSystem.contains(e.simplified()) ? 0 : 1)
                .thenComparingInt(CedictServiceImpl::lowValueRank)
                .thenComparingInt(e -> e.simplified().codePointCount(0, e.simplified().length()));
    }

    private static int lowValueRank(CedictEntry entry) {
        if (entry.definitions().isEmpty()) {
            return 1;
        }
        String first = entry.definitions().get(0).toLowerCase(Locale.ROOT);
        for (String prefix : LOW_VALUE_PREFIXES) {
            if (first.startsWith(prefix)) {
                return 1;
            }
        }
        return 0;
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
                        // Không có DB (ví dụ trong test) thì xếp hạng không dùng tiêu chí này.
                        log.warn("Không đọc được bảng words để xếp hạng từ điển: {}", e.getMessage());
                        systemWords = Set.of();
                    }
                    systemWordsLoadedAt = now;
                }
            }
        }
        return systemWords;
    }
}
