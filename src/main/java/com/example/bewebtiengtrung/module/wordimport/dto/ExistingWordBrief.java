package com.example.bewebtiengtrung.module.wordimport.dto;

/**
 * Tóm tắt từ đã có trong hệ thống trùng với dòng đang nhập.
 *
 * @param alreadyLearned người dùng hiện tại đã đánh dấu từ này là đã học hay chưa
 */
public record ExistingWordBrief(
        Long id,
        String simplified,
        String pinyin,
        String meaningVi,
        String meaningEn,
        Integer hskLevel,
        boolean alreadyLearned
) {
}
