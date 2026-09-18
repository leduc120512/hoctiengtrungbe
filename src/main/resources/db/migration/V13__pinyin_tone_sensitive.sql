-- =====================================================================
-- V13 : Cho cot words.pinyin phan biet THANH DIEU
--
-- VAN DE
-- Database dung collation mac dinh utf8mb4_0900_ai_ci — "ai" = accent
-- insensitive, tuc la BO QUA DAU. Voi tieng Viet thi tot (go "hoc" ra
-- "học"), nhung voi pinyin thi sai nghiem trong: dau thanh trong pinyin
-- KHONG phai dau phu, no la mot phan danh tinh cua tu.
--
-- Hau qua do dac duoc trong MySQL 8.0.46:
--     INSERT 好 'hǎo'  -> OK
--     INSERT 好 'hào'  -> ERROR 1062 Duplicate entry '好-hào'
-- Nghia la khoa UNIQUE(simplified, pinyin) KHONG luu duoc chu da am
-- (多音字) — mot hien tuong rat pho bien trong tieng Trung:
--     好  hǎo (tot)      / hào (thich)
--     教  jiāo (day)     / jiào (ton giao, giao duc)
--     数  shǔ (dem)      / shù (con so)
--     得  dé (duoc)      / děi (phai) / de (tro tu)
-- Cung ly do do, ham existsBySimplifiedAndPinyin cua tang service cung
-- bao trung nham khi them tu da am moi.
--
-- CACH XU LY
-- Doi rieng collation cua cot pinyin sang utf8mb4_0900_as_cs
-- (as = accent sensitive, cs = case sensitive). Cac cot khac giu nguyen
-- ai_ci de tim kiem tieng Viet va Han tu van de chiu.
--
-- Tim kiem van than thien: WordRepository da bo sung pinyin_numbered vao
-- dieu kien tim kiem, ma pinyin_numbered la chuoi ASCII ('hao3'), nen go
-- "hao" khong dau van tim ra 好.
-- =====================================================================

-- Xoa khoa unique cu truoc khi doi collation cua cot nam trong khoa.
ALTER TABLE words DROP INDEX uk_words_simplified_pinyin;

ALTER TABLE words
    MODIFY COLUMN pinyin VARCHAR(120)
        CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL;

-- Tao lai khoa unique: bay gio 好-hǎo va 好-hào la HAI dong khac nhau.
ALTER TABLE words
    ADD CONSTRAINT uk_words_simplified_pinyin UNIQUE (simplified, pinyin);

-- Chi muc tim kiem theo pinyin cung phai tao lai theo collation moi.
ALTER TABLE words DROP INDEX ix_words_pinyin;
ALTER TABLE words ADD INDEX ix_words_pinyin (pinyin);
