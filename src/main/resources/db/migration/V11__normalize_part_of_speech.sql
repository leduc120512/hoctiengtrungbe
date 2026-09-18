-- =====================================================================
-- V11 : Chuan hoa cot words.part_of_speech
--
-- Van de: du lieu seed duoc sinh theo nhieu dot nen ton tai 2 quy uoc
-- lan lon trong cung mot cot:
--     - khong dau  : 'danh tu', 'dong tu', 'tinh tu', 'dai tu', ...
--     - co dau     : 'phó từ', 'trợ từ', 'danh từ vị trí', ...
-- Tong cong 19 gia tri khac nhau cho khoang 11 tu loai. Hau qua:
--     - FE hien thi nhan khong dong nhat ("danh tu" canh "danh từ")
--     - Bo loc theo tu loai bi vo (cung mot tu loai nhung 2 gia tri)
--
-- Cach xu ly: quy het ve MOT bo tu loai tieng Viet chuan, co dau.
-- Cac tieu loai (dai tu nghi van, dong tu nang nguyen, tro tu ngu khi,
-- danh tu rieng, danh tu vi tri, cum dong tu) duoc gom ve tu loai cha
-- de bo loc tren FE chi con mot tap gia tri huu han va on dinh.
-- =====================================================================

UPDATE words
SET part_of_speech = CASE part_of_speech
    -- Danh tu va cac tieu loai
    WHEN 'danh tu'             THEN 'danh từ'
    WHEN 'danh từ vị trí'      THEN 'danh từ'
    WHEN 'danh từ riêng'       THEN 'danh từ'
    WHEN 'danh từ / động từ'   THEN 'danh từ'
    -- Dong tu va cac tieu loai
    WHEN 'dong tu'             THEN 'động từ'
    WHEN 'cụm động từ'         THEN 'động từ'
    WHEN 'động từ năng nguyện' THEN 'động từ'
    -- Tinh tu
    WHEN 'tinh tu'             THEN 'tính từ'
    -- Dai tu va tieu loai
    WHEN 'dai tu'              THEN 'đại từ'
    WHEN 'đại từ nghi vấn'     THEN 'đại từ'
    -- So tu / luong tu
    WHEN 'so tu'               THEN 'số từ'
    WHEN 'luong tu'            THEN 'lượng từ'
    -- Pho tu / gioi tu / lien tu
    WHEN 'pho tu'              THEN 'phó từ'
    WHEN 'gioi tu'             THEN 'giới từ'
    WHEN 'lien tu'             THEN 'liên từ'
    -- Tro tu va tieu loai
    WHEN 'tro tu'              THEN 'trợ từ'
    WHEN 'trợ từ ngữ khí'      THEN 'trợ từ'
    WHEN 'trợ từ kết cấu'      THEN 'trợ từ'
    -- Than tu
    WHEN 'than tu'             THEN 'thán từ'
    ELSE part_of_speech
END
WHERE part_of_speech IS NOT NULL;
