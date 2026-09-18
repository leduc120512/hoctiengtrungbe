-- =====================================================================
-- V2 : Du lieu tham chieu (reference data) cho "Web hoc tieng Trung"
-- Bao gom: roles, users (admin + demo), user_roles, topics (14 chu de)
-- MySQL 8.0 / InnoDB / utf8mb4
-- Quy uoc:
--   - Tat ca id deu ghi TUONG MINH de cac file seed sau (V3, V4, ...)
--     tham chieu khoa ngoai mot cach xac dinh.
--   - Moc thoi gian chuan cua bo seed: 2026-01-01 00:00:00.000000 (UTC)
--   - Cot BIT(1) dung b'1' / b'0'
-- Phan bo id da thong nhat:
--   topics 1..14 | words 1..300 | courses 1..6 | lessons 1..60
--   quizzes 1..12 | questions 1..240 | decks 1..10
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. ROLES - vai tro he thong
-- ---------------------------------------------------------------------
INSERT INTO roles (id, name, description) VALUES
  (1, 'ROLE_USER',  'Người học'),
  (2, 'ROLE_ADMIN', 'Quản trị viên');

-- ---------------------------------------------------------------------
-- 2. USERS - hai tai khoan mac dinh
-- ---------------------------------------------------------------------
-- Hai tai khoan mac dinh dung de thu API ngay sau khi import.
-- Mat khau (da hash bang BCrypt cost 10, da kiem chung bang BCryptPasswordEncoder.matches):
--     admin@hoctiengtrung.vn  ->  Admin@123   (ROLE_USER + ROLE_ADMIN)
--     demo@hoctiengtrung.vn   ->  Demo@123    (ROLE_USER)
-- CANH BAO: day la mat khau demo. Doi ngay truoc khi deploy that.
INSERT INTO users (id, email, username, password_hash, display_name, avatar_url,
                   native_language, current_hsk_level, status, last_login_at,
                   created_at, updated_at) VALUES
  (1, 'admin@hoctiengtrung.vn', 'admin',
      '$2a$10$7XLVW7ejXNjsMDzRX6mMWOzDGP.qqEU65E0SsulKL84AvorEEo9fO',
      'Quản trị viên', NULL, 'vi', 6, 'ACTIVE', NULL,
      '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (2, 'demo@hoctiengtrung.vn', 'demo',
      '$2a$10$808DUXNyIb2fFbHo/TCUMOWDfG2sW3Ipi2gkLsFQmP7d/.yZnNHE2',
      'Học viên demo', NULL, 'vi', 1, 'ACTIVE', NULL,
      '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- ---------------------------------------------------------------------
-- 3. USER_ROLES - gan vai tro
--    user 1 (admin) : ROLE_USER + ROLE_ADMIN
--    user 2 (demo)  : ROLE_USER
-- ---------------------------------------------------------------------
INSERT INTO user_roles (user_id, role_id) VALUES
  (1, 1),
  (1, 2),
  (2, 1);

-- ---------------------------------------------------------------------
-- 4. TOPICS - 14 chu de tu vung, id 1..14, sort_order = id
--    Thu tu slug la CO DINH: cac file seed khac tham chieu topic theo id.
-- ---------------------------------------------------------------------
INSERT INTO topics (id, slug, name_vi, name_zh, description, icon, sort_order, created_at, updated_at) VALUES
  (1,  'chao-hoi', 'Chào hỏi', '问候',
       'Các mẫu câu chào hỏi, làm quen, giới thiệu bản thân và tạm biệt trong giao tiếp hằng ngày.',
       '👋', 1, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (2,  'gia-dinh', 'Gia đình', '家庭',
       'Từ vựng về các thành viên trong gia đình, quan hệ họ hàng và cách xưng hô.',
       '👨‍👩‍👧', 2, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (3,  'so-dem', 'Số đếm', '数字',
       'Chữ số, cách đọc số, đếm tuổi, đếm tiền và các lượng từ cơ bản đi kèm số.',
       '🔢', 3, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (4,  'thoi-gian', 'Thời gian', '时间',
       'Ngày, tháng, năm, thứ, giờ giấc và các trạng từ chỉ thời gian thường gặp.',
       '🕐', 4, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (5,  'mau-sac', 'Màu sắc', '颜色',
       'Tên các màu sắc cơ bản và cách miêu tả màu của đồ vật.',
       '🎨', 5, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (6,  'do-an', 'Đồ ăn và thức uống', '饮食',
       'Món ăn, đồ uống, hoa quả, cách gọi món và nói về khẩu vị.',
       '🍜', 6, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (7,  'hoc-tap', 'Học tập', '学习',
       'Từ vựng về trường lớp, thầy cô, bạn học, sách vở và hoạt động học tiếng Trung.',
       '📚', 7, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (8,  'cong-viec', 'Công việc', '工作',
       'Nghề nghiệp, nơi làm việc, đồng nghiệp và các hoạt động thường ngày ở công ty.',
       '💼', 8, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (9,  'mua-sam', 'Mua sắm', '购物',
       'Đi chợ, siêu thị, hỏi giá, mặc cả, thanh toán và mô tả hàng hoá.',
       '🛒', 9, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (10, 'giao-thong', 'Giao thông', '交通',
       'Phương tiện đi lại, hỏi đường, chỉ đường và các địa điểm công cộng.',
       '🚌', 10, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (11, 'thoi-tiet', 'Thời tiết', '天气',
       'Các hiện tượng thời tiết, bốn mùa, nhiệt độ và cách hỏi đáp về thời tiết.',
       '☀️', 11, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (12, 'co-the-suc-khoe', 'Cơ thể và sức khoẻ', '身体与健康',
       'Bộ phận cơ thể, triệu chứng bệnh, đi khám bác sĩ và lời khuyên giữ gìn sức khoẻ.',
       '🩺', 12, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (13, 'nha-cua', 'Nhà cửa', '房屋',
       'Các phòng trong nhà, đồ đạc nội thất và hoạt động sinh hoạt tại nhà.',
       '🏠', 13, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
  (14, 'dong-tu-thong-dung', 'Động từ thông dụng', '常用动词',
       'Nhóm động từ xuất hiện nhiều nhất ở trình độ HSK1 - HSK2, dùng cho mọi chủ đề.',
       '⚡', 14, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');
