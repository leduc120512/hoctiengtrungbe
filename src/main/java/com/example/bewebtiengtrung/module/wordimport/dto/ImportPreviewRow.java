package com.example.bewebtiengtrung.module.wordimport.dto;

import java.util.List;

/**
 * Kết quả duyệt trước của MỘT dòng — đã được chuẩn hoá, tra từ điển và đối chiếu với hệ thống.
 *
 * @param index                   vị trí dòng trong lô (bắt đầu từ 0, khớp thứ tự {@code rows} gửi lên)
 * @param input                   dữ liệu thô người dùng gửi (đã trim)
 * @param pinyinMatchesDictionary {@code null} khi người dùng không nhập pinyin hoặc từ điển không có mục
 * @param dictionaryPinyin        pinyin có dấu của mục từ điển đầu tiên (để FE gợi ý sửa)
 * @param suggestedAction         hành động đề xuất, FE có thể đổi trước khi confirm
 * @param messages                thông điệp tiếng Việt giải thích trạng thái
 */
public record ImportPreviewRow(
        int index,
        ImportRowInput input,
        String simplified,
        String traditional,
        String pinyin,
        String pinyinNumbered,
        String meaningVi,
        String meaningEn,
        Integer hskLevel,
        boolean dictionaryFound,
        Boolean pinyinMatchesDictionary,
        String dictionaryPinyin,
        List<DictionarySenseResponse> dictionarySenses,
        List<ImportCandidateResponse> candidates,
        ExistingWordBrief existingWord,
        ImportRowStatus status,
        ImportAction suggestedAction,
        List<String> messages
) {
}
