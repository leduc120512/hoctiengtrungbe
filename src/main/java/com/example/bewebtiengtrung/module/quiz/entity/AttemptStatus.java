package com.example.bewebtiengtrung.module.quiz.entity;

/** Trạng thái của một lần làm bài kiểm tra. */
public enum AttemptStatus {

    /** Đang làm bài, chưa nộp. */
    IN_PROGRESS,

    /** Đã nộp bài và đã được chấm điểm. */
    SUBMITTED,

    /** Quá thời gian cho phép, bài làm bị huỷ. */
    EXPIRED
}
