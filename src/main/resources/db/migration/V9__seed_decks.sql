-- =====================================================================
-- V9 : Seed bo the ghi nho (flashcard decks) he thong + the (flashcards)
-- MySQL 8.0 / InnoDB / utf8mb4
-- ---------------------------------------------------------------------
-- Pham vi id co dinh cua file nay:
--   decks      : id 1..10 (id tuong minh, dung cho FK cheo file)
--   flashcards : AUTO_INCREMENT (khong co bang nao tham chieu toi)
-- Quy uoc:
--   - deck he thong : owner_id = NULL, is_system = b'1', is_public = b'1'
--   - deck 1..2     : theo cap do HSK (hsk_level 1 / 2, topic_id = NULL)
--   - deck 3..10    : theo chu de (topic_id 1..8, hsk_level = NULL)
--   - The duoc sinh bang INSERT ... SELECT tu bang words de noi dung the
--     KHONG BAO GIO lech voi du lieu tu vung da seed o file khac.
--   - card_count duoc tinh lai o cuoi file bang 1 cau UPDATE duy nhat,
--     gioi han WHERE d.id BETWEEN 1 AND 10 de chi cham vao han muc id
--     cua file nay va chay duoc ca khi sql_safe_updates = 1.
-- Phu thuoc (phai duoc seed TRUOC file nay):
--   topics      : id 1..8 (V2 seed du 1..14, file nay chi dung 8 chu de dau)
--   words       : id 1..300 (HSK1 1..150, HSK2 151..300)
--   word_topics : bang noi words <-> topics, dung cho deck 3..10
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. DECKS (id 1..10)
-- ---------------------------------------------------------------------
INSERT INTO decks (id, owner_id, topic_id, slug, name, description, hsk_level, is_public, is_system, card_count, created_at, updated_at) VALUES
(1,  NULL, NULL, 'hsk1-tron-bo',           'HSK 1 - Trọn bộ',            'Trọn bộ từ vựng HSK cấp 1. Học và ôn tập theo thuật toán lặp lại ngắt quãng SM-2, phù hợp cho người mới bắt đầu học tiếng Trung.', 1,    b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(2,  NULL, NULL, 'hsk2-tron-bo',           'HSK 2 - Trọn bộ',            'Trọn bộ từ vựng HSK cấp 2. Mở rộng vốn từ sau khi đã nắm vững HSK 1, chuẩn bị cho kỳ thi HSK cấp 2.', 2,    b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(3,  NULL, 1,    'chu-de-chao-hoi',        'Chủ đề: Chào hỏi',           'Từ vựng chào hỏi, làm quen và giao tiếp cơ bản hằng ngày bằng tiếng Trung.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(4,  NULL, 2,    'chu-de-gia-dinh',        'Chủ đề: Gia đình',           'Từ vựng về các thành viên trong gia đình và quan hệ họ hàng.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(5,  NULL, 3,    'chu-de-so-dem',          'Chủ đề: Số đếm',             'Từ vựng về số đếm, số lượng và cách dùng lượng từ trong tiếng Trung.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(6,  NULL, 4,    'chu-de-thoi-gian',       'Chủ đề: Thời gian',          'Từ vựng về ngày tháng, giờ giấc, thứ trong tuần và các mốc thời gian.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(7,  NULL, 5,    'chu-de-mau-sac',         'Chủ đề: Màu sắc',            'Từ vựng về màu sắc và cách miêu tả màu sắc của sự vật.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(8,  NULL, 6,    'chu-de-do-an-thuc-uong', 'Chủ đề: Đồ ăn và thức uống', 'Từ vựng về món ăn, đồ uống và các hoạt động ăn uống thường ngày.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(9,  NULL, 7,    'chu-de-hoc-tap',         'Chủ đề: Học tập',            'Từ vựng về trường lớp, sách vở, thầy cô và việc học tiếng Trung.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(10, NULL, 8,    'chu-de-cong-viec',       'Chủ đề: Công việc',          'Từ vựng về nghề nghiệp, nơi làm việc và các hoạt động trong công việc.', NULL, b'1', b'1', 0, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- ---------------------------------------------------------------------
-- 2. FLASHCARDS (sinh tu bang words)
--    front      = chu Han gian the
--    back       = "pinyin - nghia tieng Viet"
--    hint       = tu loai (part_of_speech), co the NULL
--    audio_url  = lay theo tu vung neu co (NULL neu tu vung chua co file)
--    image_url  = lay theo tu vung neu co (NULL neu tu vung chua co anh)
--    sort_order = thu tu 0-based theo words.id, dung ROW_NUMBER de luon
--                 lien tuc ke ca khi id tu vung khong lien tiep
--    Deck chu de gioi han 20 the bang WHERE t.rn <= 20 o truy van ngoai,
--    KHONG dung ORDER BY + LIMIT trong derived table (MySQL duoc phep bo
--    qua ORDER BY trong bang dan xuat, se lam sort_order khong xac dinh)
-- ---------------------------------------------------------------------

-- Deck 1: HSK 1 - Tron bo (toan bo tu vung HSK cap 1)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 1,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    WHERE w.hsk_level = 1
) t;

-- Deck 2: HSK 2 - Tron bo (toan bo tu vung HSK cap 2)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 2,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    WHERE w.hsk_level = 2
) t;

-- Deck 3: Chu de Chao hoi (topic_id = 1, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 3,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 1
) t
WHERE t.rn <= 20;

-- Deck 4: Chu de Gia dinh (topic_id = 2, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 4,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 2
) t
WHERE t.rn <= 20;

-- Deck 5: Chu de So dem (topic_id = 3, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 5,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 3
) t
WHERE t.rn <= 20;

-- Deck 6: Chu de Thoi gian (topic_id = 4, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 6,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 4
) t
WHERE t.rn <= 20;

-- Deck 7: Chu de Mau sac (topic_id = 5, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 7,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 5
) t
WHERE t.rn <= 20;

-- Deck 8: Chu de Do an va thuc uong (topic_id = 6, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 8,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 6
) t
WHERE t.rn <= 20;

-- Deck 9: Chu de Hoc tap (topic_id = 7, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 9,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 7
) t
WHERE t.rn <= 20;

-- Deck 10: Chu de Cong viec (topic_id = 8, toi da 20 the)
INSERT INTO flashcards (deck_id, word_id, front, back, hint, audio_url, image_url, sort_order, created_at, updated_at)
SELECT 10,
       t.id,
       t.simplified,
       CONCAT(t.pinyin, ' - ', t.meaning_vi),
       t.part_of_speech,
       t.audio_url,
       t.image_url,
       t.rn - 1,
       '2026-01-01 00:00:00.000000',
       '2026-01-01 00:00:00.000000'
FROM (
    SELECT w.id,
           w.simplified,
           w.pinyin,
           w.meaning_vi,
           w.part_of_speech,
           w.audio_url,
           w.image_url,
           ROW_NUMBER() OVER (ORDER BY w.id) AS rn
    FROM words w
    JOIN word_topics wt ON wt.word_id = w.id
    WHERE wt.topic_id = 8
) t
WHERE t.rn <= 20;

-- ---------------------------------------------------------------------
-- 3. Tinh lai so the thuc te cua tung deck (card_count)
-- ---------------------------------------------------------------------
UPDATE decks d
SET d.card_count = (SELECT COUNT(*) FROM flashcards f WHERE f.deck_id = d.id)
WHERE d.id BETWEEN 1 AND 10;
