-- =====================================================================
-- V15 : Bang user_sentences — "Cau cua toi"
--
-- Moi nguoi dung co mot kho cau luyen nghe ghep tu dung nhung tu da hoc
-- (bang user_words, V14). Cau co the den tu ba nguon (cot source):
--     BUILTIN : seed san (90 cau goc cua trang "Cau cua toi")
--     MANUAL  : nguoi dung tu dan vao
--     AI      : Claude sinh ra tu von tu da hoc (module ai)
--
-- hanzi_key = hanzi da bo moi dau cau va khoang trang, chi giu ky tu CJK
-- (tinh bang SentenceText.key trong code) — dung lam khoa chong trung,
-- vi cung mot cau co the duoc ghi voi dau cau Trung/Viet khac nhau.
--
-- Bang KHONG co updated_at => entity khong ke thua BaseEntity, created_at
-- duoc set trong code.
-- =====================================================================

CREATE TABLE user_sentences (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    hanzi      VARCHAR(200) NOT NULL,
    pinyin     VARCHAR(400) NOT NULL,
    meaning_vi VARCHAR(500) NOT NULL,
    level      INT          NOT NULL DEFAULT 1,
    source     VARCHAR(20)  NOT NULL DEFAULT 'MANUAL',
    hanzi_key  VARCHAR(200) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_sentences_user_key (user_id, hanzi_key),
    KEY ix_user_sentences_user_level (user_id, level),
    CONSTRAINT fk_user_sentences_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
