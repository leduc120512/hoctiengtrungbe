-- =====================================================================
-- V12 : Dong bo lai cot flashcards.hint theo words.part_of_speech
--
-- Van de (loi thu tu migration):
--   V9  tao flashcard bang INSERT ... SELECT, copy words.part_of_speech
--       sang cot hint.
--   V11 chuan hoa words.part_of_speech ('danh tu' -> 'danh từ', gom cac
--       tieu loai ve tu loai cha).
--   Nhung V11 chi sua BANG NGUON. Ban sao da nam trong flashcards.hint
--       van giu gia tri cu, nen mat dong bo:
--         words.part_of_speech = 'danh từ'   (da chuan)
--         flashcards.hint      = 'danh tu'   (con cu)
--
-- Hau qua neu khong sua: mat truoc the flashcard hien nhan tu loai khong
-- dau, lech han voi nhan hien o trang tu vung cho cung mot tu.
--
-- Cach xu ly: lay lai hint truc tiep tu bang words. Toan bo hint hien
-- tai deu co nguon goc tu part_of_speech nen ghi de la an toan; lenh nay
-- cung tu dong dung cho moi lan chay lai.
-- =====================================================================

-- LUU Y: database dung collation utf8mb4_0900_ai_ci (accent-insensitive),
-- nghia la 'danh tu' = 'danh từ' khi so sanh thong thuong. Neu viet
-- "WHERE f.hint <> w.part_of_speech" thi dieu kien luon SAI va lenh UPDATE
-- khong dong nao duoc cap nhat. Bat buoc ep COLLATE utf8mb4_bin de so
-- sanh dung tung byte.
UPDATE flashcards f
JOIN words w ON w.id = f.word_id
SET f.hint = w.part_of_speech
WHERE f.word_id IS NOT NULL
  AND (f.hint IS NULL
       OR f.hint COLLATE utf8mb4_bin <> w.part_of_speech COLLATE utf8mb4_bin);
