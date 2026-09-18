-- =====================================================================
-- V10 : Bo sung chu de "Hu tu & ngu phap" (id 15)
--
-- Ly do: 14 chu de dau tien deu la chu de theo CHU DIEM (chao hoi, gia dinh,
-- so dem, mau sac...). Nhung tu cong cu nhu 虽然 / 所以 / 已经 / 正在 / 着
-- khong thuoc chu diem nao, nen truoc do bi "mo coi": khong hien thi khi
-- nguoi hoc duyet tu vung theo chu de.
--
-- Cau lenh gan chu de duoi day chay theo DU LIEU (moi tu chua co chu de),
-- nen no tu dong bao phu het, khong can liet ke tay tung id.
-- =====================================================================

INSERT INTO topics (id, slug, name_vi, name_zh, description, icon, sort_order,
                    created_at, updated_at)
VALUES (15, 'hu-tu-ngu-phap', 'Hư từ & ngữ pháp', '虚词与语法',
        'Liên từ, phó từ, trợ từ và đại từ nghi vấn - nhóm từ công cụ dùng để nối câu và diễn đạt quan hệ ngữ pháp.',
        'grammar', 15,
        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- Gan chu de 15 cho MOI tu hien chua co chu de nao.
INSERT INTO word_topics (word_id, topic_id)
SELECT w.id, 15
FROM words w
WHERE NOT EXISTS (SELECT 1 FROM word_topics wt WHERE wt.word_id = w.id);
