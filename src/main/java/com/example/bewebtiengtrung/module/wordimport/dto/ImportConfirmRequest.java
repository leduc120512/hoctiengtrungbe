package com.example.bewebtiengtrung.module.wordimport.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Yêu cầu thực thi nhập từ vựng sau khi người dùng đã duyệt.
 *
 * @param markAsLearned {@code true} thì mọi từ vừa CREATE/UPDATE/LINK được đánh dấu đã học cho người dùng hiện tại
 */
public record ImportConfirmRequest(

        boolean markAsLearned,

        @NotEmpty(message = "Danh sách dòng không được để trống")
        @Size(max = 500, message = "Mỗi lần nhập tối đa 500 dòng")
        List<@Valid ImportConfirmRow> rows
) {
}
