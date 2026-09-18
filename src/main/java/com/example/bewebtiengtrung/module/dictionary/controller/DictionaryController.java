package com.example.bewebtiengtrung.module.dictionary.controller;

import com.example.bewebtiengtrung.module.dictionary.dto.DictionaryEntryResponse;
import com.example.bewebtiengtrung.module.dictionary.dto.PinyinConvertResponse;
import com.example.bewebtiengtrung.module.dictionary.service.CedictService;
import com.example.bewebtiengtrung.module.dictionary.service.PinyinUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Tra từ điển CC-CEDICT. Yêu cầu đăng nhập (quy tắc mặc định của SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/dictionary")
@RequiredArgsConstructor
@Tag(name = "Từ điển", description = "Tra CC-CEDICT: theo chữ Hán, theo pinyin, theo nghĩa tiếng Anh")
public class DictionaryController {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final CedictService cedictService;

    @GetMapping("/lookup")
    @Operation(summary = "Tra theo chữ giản thể (trả về mọi cách đọc của từ đa âm)")
    public List<DictionaryEntryResponse> lookup(@RequestParam("q") String q) {
        return cedictService.lookupSimplified(q).stream().map(DictionaryEntryResponse::from).toList();
    }

    @GetMapping("/reverse")
    @Operation(summary = "Tra ngược theo pinyin (có dấu hoặc dạng số), đã xếp hạng")
    public List<DictionaryEntryResponse> reverse(@RequestParam("pinyin") String pinyin,
                                                 @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return cedictService.reverseLookup(pinyin, clamp(limit)).stream()
                .map(DictionaryEntryResponse::from).toList();
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm Anh ↔ Trung: nghĩa tiếng Anh hoặc chữ Hán bắt đầu bằng q")
    public List<DictionaryEntryResponse> search(@RequestParam("q") String q,
                                                @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return cedictService.search(q, clamp(limit)).stream().map(DictionaryEntryResponse::from).toList();
    }

    @GetMapping("/pinyin")
    @Operation(summary = "Chuyển đổi pinyin giữa dạng dấu và dạng số")
    public PinyinConvertResponse convert(@RequestParam("text") String text) {
        String numbered = PinyinUtils.markedToNumbered(text);
        return new PinyinConvertResponse(PinyinUtils.numberedToMarked(numbered, true), numbered,
                PinyinUtils.normalizeKey(text));
    }

    private static int clamp(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
