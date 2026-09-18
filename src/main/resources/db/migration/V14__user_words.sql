-- =====================================================================
-- V14 : So tu da hoc cua nguoi dung (user_words)
--
-- Moi cap (user_id, word_id) chi ton tai dung mot dong; status la
-- LEARNING / LEARNED / MASTERED. learned_at la thoi diem tu duoc them
-- vao so (dung de thong ke "hoc trong tuan nay").
-- Xoa user hoac xoa tu goc thi dong tuong ung tu mat (ON DELETE CASCADE).
-- =====================================================================

CREATE TABLE user_words (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    word_id    BIGINT       NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'LEARNED',
    note       VARCHAR(500) NULL,
    learned_at DATETIME(6)  NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_words_user_word (user_id, word_id),
    KEY ix_user_words_word (word_id),
    KEY ix_user_words_user_status (user_id, status),
    CONSTRAINT fk_user_words_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_words_word FOREIGN KEY (word_id) REFERENCES words (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
