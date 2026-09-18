package com.example.bewebtiengtrung.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Kiểu trả về chuẩn cho mọi endpoint có phân trang.
 * Controller không bao giờ trả thẳng {@link Page} ra ngoài (tránh lộ cấu trúc nội bộ của Spring Data).
 *
 * @param <T> kiểu DTO của phần tử trong trang
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    /** Kích thước trang mặc định của dự án ({@code @PageableDefault(size = 20)}). */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Bọc một {@link Page} đã map sẵn sang DTO. */
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    /** Bọc một {@link Page} entity và map từng phần tử sang DTO bằng {@code mapper}. */
    public static <E, T> PageResponse<T> of(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    /** Trang rỗng (dùng khi không có dữ liệu mà vẫn phải giữ đúng cấu trúc trả về). */
    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(List.of(), page, size, 0L, 0, true, true);
    }

    /** Trang rỗng đầu tiên với kích thước trang mặc định 20 (theo quy ước phân trang của dự án). */
    public static <T> PageResponse<T> empty() {
        return empty(0, DEFAULT_PAGE_SIZE);
    }
}
