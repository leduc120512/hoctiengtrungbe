-- =====================================================================
-- V17 : Tai khoan don gian de dang nhap nhanh (theo yeu cau nguoi dung —
--       site ca nhan, khong can bao mat cao)
--
--   2222 / 2222  = tai khoan CUA CHU SITE (chinh la user id 1, admin@hoctiengtrung.vn,
--                  dang giu 89 tu da hoc + 90 cau). Chi doi username + mat khau.
--   1111 / 1111  = tai khoan KHACH cho nguoi khac (user moi, ROLE_USER),
--                  duoc chep san 89 tu da hoc + 90 cau de dung duoc ngay.
--
-- Dang nhap chap nhan ca email lan username (xem CustomUserDetailsService).
-- Hash BCrypt cost 10, da kiem chung bang BCryptPasswordEncoder.matches.
-- =====================================================================

-- 1. Tai khoan cua chu site: username 2222, mat khau 2222
UPDATE users
SET username = '2222',
    password_hash = '$2a$10$d8vCo2so0WOIJgww3ybnVuyuIS0ZCu.a7RIb3EKIrEX9Tp1gTbv32',
    display_name = 'Tôi',
    updated_at = NOW(6)
WHERE id = 1;

-- 2. Tai khoan khach: username 1111, mat khau 1111 (idempotent)
INSERT INTO users (email, username, password_hash, display_name, avatar_url, native_language,
                   current_hsk_level, status, last_login_at, created_at, updated_at)
SELECT '1111@hoctiengtrung.vn', '1111',
       '$2a$10$LTseSfHnQp3m6nIE39dchuT3YHAZePEz3mhwyJUgu0CCu3ZlPuywC',
       'Khách', NULL, 'vi', 1, 'ACTIVE', NULL, NOW(6), NOW(6)
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.username = '1111');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_USER'
WHERE u.username = '1111'
  AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- 3. Chep 89 tu da hoc cua chu site sang tai khoan khach
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT g.id, uw.word_id, uw.status, uw.note, uw.learned_at, NOW(6), NOW(6)
FROM user_words uw
JOIN users g ON g.username = '1111'
WHERE uw.user_id = 1
  AND NOT EXISTS (SELECT 1 FROM user_words x WHERE x.user_id = g.id AND x.word_id = uw.word_id);

-- 4. Chep 90 cau co san sang tai khoan khach
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT g.id, s.hanzi, s.pinyin, s.meaning_vi, s.level, s.source, s.hanzi_key, NOW(6)
FROM user_sentences s
JOIN users g ON g.username = '1111'
WHERE s.user_id = 1 AND s.source = 'BUILTIN'
  AND NOT EXISTS (SELECT 1 FROM user_sentences x WHERE x.user_id = g.id AND x.hanzi_key = s.hanzi_key);
