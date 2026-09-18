package com.example.bewebtiengtrung.module.dictionary.dto;

import com.example.bewebtiengtrung.module.dictionary.service.CedictEntry;

import java.util.List;

/** Một mục từ điển CC-CEDICT trả về cho client. */
public record DictionaryEntryResponse(String traditional,
                                      String simplified,
                                      String pinyinNumbered,
                                      String pinyinMarked,
                                      List<String> definitions) {

    public static DictionaryEntryResponse from(CedictEntry entry) {
        return new DictionaryEntryResponse(entry.traditional(), entry.simplified(),
                entry.pinyinNumbered(), entry.pinyinMarked(), entry.definitions());
    }
}
