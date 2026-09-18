-- =====================================================================
-- V16 : Seed so tu vung ca nhan cua nguoi dung (user_id = 1, admin@hoctiengtrung.vn)
-- Nguon: 89 tu nguoi hoc khai da hoc (bang 79 tu + PDF "HSK1 toan bo tu vung da hoc")
--        va 90 cau luyen nghe ghep tu dung 89 tu do (tu ung dung "Moi Ngay Zhongwen").
-- Sinh tu dong tu JSON; idempotent (chay lai khong loi, khong nhan doi).
-- Cot words.pinyin dung collation utf8mb4_0900_as_cs (V13). So khop theo dang chuan hoa: bo khoang
-- trang, chu thuong, VAN phan biet thanh dieu — de 'xià yǔ' va 'xiàyǔ' la mot tu, 'hǎo' va 'hào' van la hai.
-- =====================================================================

-- 1. Them nhung tu chua co trong bang words (81/89 da co san tu seed HSK1-2)
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '开车', '開車', 'kāichē', 'kai1 che1', 'lái xe', 'to drive a car', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '开车' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('kāichē', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '吃饭', '吃飯', 'chīfàn', 'chi1 fan4', 'ăn cơm', 'to have a meal; to eat', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '吃饭' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('chīfàn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '打电话', '打电话', 'dǎ diànhuà', 'da3 dian4 hua4', 'gọi điện thoại', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '打电话' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('dǎ diànhuà', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '昨天', '昨天', 'zuótiān', 'zuo2 tian1', 'hôm qua', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '昨天' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zuótiān', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '今天', '今天', 'jīntiān', 'jin1 tian1', 'hôm nay', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '今天' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jīntiān', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '明天', '明天', 'míngtiān', 'ming2 tian1', 'ngày mai', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '明天' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('míngtiān', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '年', '年', 'nián', 'nian2', 'năm', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '年' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nián', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '下雨', '下雨', 'xiàyǔ', 'xia4 yu3', 'trời mưa', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '下雨' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xiàyǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '喝水', '喝水', 'hē shuǐ', 'he1 shui3', 'uống nước', 'to drink water', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '喝水' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hē shuǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '做什么', '做什麼', 'zuò shénme', 'zuo4 shen2 me5', 'làm gì', 'to do what; what are you doing', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '做什么' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zuò shénme', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '去哪儿', '去哪兒', 'qù nǎr', 'qu4 nar3', 'đi đâu', 'where to go; where are you going', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '去哪儿' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qù nǎr', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '我', '我', 'wǒ', 'wo3', 'tôi', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '我' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('wǒ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '和', '和', 'hé', 'he2', 'và', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '和' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hé', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '你', '你', 'nǐ', 'ni3', 'bạn', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '你' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '对吗', '對嗎', 'duì ma', 'dui4 ma5', 'đúng không', 'right?; is that correct?', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '对吗' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('duì ma', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '妹妹', '妹妹', 'mèimei', 'mei4 mei5', 'em gái', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '妹妹' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('mèimei', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '他们', '他們', 'tāmen', 'ta1 men5', 'họ', 'they; them', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '他们' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('tāmen', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '姐姐', '姐姐', 'jiějie', 'jie3 jie5', 'chị gái', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '姐姐' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jiějie', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '儿子', '儿子', 'érzi', 'er2 zi5', 'con trai', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '儿子' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('érzi', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '衣服', '衣服', 'yīfu', 'yi1 fu5', 'quần áo', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '衣服' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yīfu', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '请', '请', 'qǐng', 'qing3', 'mời, vui lòng', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '请' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qǐng', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '喝', '喝', 'hē', 'he1', 'uống', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '喝' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hē', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '茶', '茶', 'chá', 'cha2', 'trà', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '茶' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('chá', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '在', '在', 'zài', 'zai4', 'ở, tại; đang', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '在' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zài', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '商店', '商店', 'shāngdiàn', 'shang1 dian4', 'cửa hàng', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '商店' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shāngdiàn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '里', '里', 'lǐ', 'li3', 'bên trong', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '里' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('lǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '三', '三', 'sān', 'san1', 'ba', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '三' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('sān', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '本', '本', 'běn', 'ben3', 'quyển, cuốn', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '本' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('běn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '书', '书', 'shū', 'shu1', 'sách', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '书' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shū', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '很', '很', 'hěn', 'hen3', 'rất', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '很' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hěn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '漂亮', '漂亮', 'piàoliang', 'piao4 liang5', 'đẹp, xinh', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '漂亮' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('piàoliang', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '他', '他', 'tā', 'ta1', 'anh ấy', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '他' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('tā', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '睡觉', '睡觉', 'shuìjiào', 'shui4 jiao4', 'ngủ', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '睡觉' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shuìjiào', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '呢', '呢', 'ne', 'ne5', 'trợ từ — “còn… thì sao”', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '呢' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('ne', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '太', '太', 'tài', 'tai4', 'quá', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '太' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('tài', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '热', '热', 'rè', 're4', 'nóng', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '热' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('rè', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '了', '了', 'le', 'le5', 'trợ từ — “đã, rồi”', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '了' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('le', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '是', '是', 'shì', 'shi4', 'là', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '是' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shì', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '有', '有', 'yǒu', 'you3', 'có', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '有' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yǒu', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '不', '不', 'bù', 'bu4', 'không', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '不' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('bù', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '好', '好', 'hǎo', 'hao3', 'tốt, khỏe', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '好' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hǎo', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '人', '人', 'rén', 'ren2', 'người', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '人' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('rén', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '家', '家', 'jiā', 'jia1', 'nhà, gia đình', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '家' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jiā', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '学校', '学校', 'xuéxiào', 'xue2 xiao4', 'trường học', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '学校' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xuéxiào', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '老师', '老师', 'lǎoshī', 'lao3 shi1', 'giáo viên', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '老师' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('lǎoshī', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '学生', '学生', 'xuésheng', 'xue2 sheng5', 'học sinh', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '学生' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xuésheng', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '朋友', '朋友', 'péngyou', 'peng2 you5', 'bạn bè', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '朋友' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('péngyou', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '早上', '早上', 'zǎoshang', 'zao3 shang5', 'buổi sáng', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '早上' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zǎoshang', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '中午', '中午', 'zhōngwǔ', 'zhong1 wu3', 'buổi trưa', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '中午' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zhōngwǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '晚上', '晚上', 'wǎnshang', 'wan3 shang5', 'buổi tối', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '晚上' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('wǎnshang', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '一', '一', 'yī', 'yi1', 'một', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '一' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yī', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '二', '二', 'èr', 'er4', 'hai', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '二' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('èr', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '四', '四', 'sì', 'si4', 'bốn', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '四' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('sì', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '五', '五', 'wǔ', 'wu3', 'năm', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '五' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('wǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '六', '六', 'liù', 'liu4', 'sáu', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '六' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('liù', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '七', '七', 'qī', 'qi1', 'bảy', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '七' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qī', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '八', '八', 'bā', 'ba1', 'tám', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '八' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('bā', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '九', '九', 'jiǔ', 'jiu3', 'chín', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '九' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jiǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '十', '十', 'shí', 'shi2', 'mười', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '十' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shí', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '点', '点', 'diǎn', 'dian3', 'giờ', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '点' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('diǎn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '分钟', '分钟', 'fēnzhōng', 'fen1 zhong1', 'phút', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '分钟' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('fēnzhōng', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '来', '来', 'lái', 'lai2', 'đến', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '来' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('lái', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '回', '回', 'huí', 'hui2', 'về, quay về', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '回' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('huí', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '看', '看', 'kàn', 'kan4', 'xem, nhìn', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '看' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('kàn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '电视', '电视', 'diànshì', 'dian4 shi4', 'tivi', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '电视' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('diànshì', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '工作', '工作', 'gōngzuò', 'gong1 zuo4', 'làm việc', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '工作' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('gōngzuò', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '会', '会', 'huì', 'hui4', 'biết, có thể', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '会' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('huì', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '爸爸', '爸爸', 'bàba', 'ba4 ba5', 'bố', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '爸爸' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('bàba', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '妈妈', '妈妈', 'māma', 'ma1 ma5', 'mẹ', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '妈妈' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('māma', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '现在', '现在', 'xiànzài', 'xian4 zai4', 'bây giờ', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '现在' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xiànzài', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '几', '几', 'jǐ', 'ji3', 'mấy, bao nhiêu', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '几' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '多少', '多少', 'duōshao', 'duo1 shao5', 'bao nhiêu', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '多少' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('duōshao', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '什么', '什么', 'shénme', 'shen2 me5', 'cái gì', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '什么' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shénme', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '哪', '哪', 'nǎ', 'na3', 'nào', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '哪' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nǎ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '这', '这', 'zhè', 'zhe4', 'này, đây', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '这' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zhè', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '那', '那', 'nà', 'na4', 'kia, đó', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '那' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nà', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '买', '买', 'mǎi', 'mai3', 'mua', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '买' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('mǎi', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '钱', '钱', 'qián', 'qian2', 'tiền', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '钱' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qián', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '东西', '东西', 'dōngxi', 'dong1 xi5', 'đồ, đồ vật', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '东西' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('dōngxi', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '学习', '学习', 'xuéxí', 'xue2 xi2', 'học', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '学习' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xuéxí', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '汉语', '汉语', 'hànyǔ', 'han4 yu3', 'tiếng Trung', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '汉语' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hànyǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '说', '說', 'shuō', 'shuo1', 'nói', 'to speak; to say; to talk', NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '说' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shuō', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '写', '写', 'xiě', 'xie3', 'viết', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '写' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xiě', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '字', '字', 'zì', 'zi4', 'chữ Hán', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '字' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zì', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '医生', '医生', 'yīshēng', 'yi1 sheng1', 'bác sĩ', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '医生' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yīshēng', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '医院', '医院', 'yīyuàn', 'yi1 yuan4', 'bệnh viện', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '医院' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yīyuàn', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '苹果', '苹果', 'píngguǒ', 'ping2 guo3', 'táo', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '苹果' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('píngguǒ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '水果', '水果', 'shuǐguǒ', 'shui3 guo3', 'hoa quả', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '水果' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shuǐguǒ', ' ', '')) COLLATE utf8mb4_0900_as_cs);
INSERT INTO words (simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, created_at, updated_at)
SELECT '坐', '坐', 'zuò', 'zuo4', 'ngồi; đi bằng phương tiện', NULL, NULL, 1, NOW(6), NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM words w WHERE w.simplified = '坐' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zuò', ' ', '')) COLLATE utf8mb4_0900_as_cs);

-- 2. Danh dau 89 tu la DA HOC cho user 1
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '开车' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('kāichē', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '吃饭' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('chīfàn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '打电话' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('dǎ diànhuà', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '昨天' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zuótiān', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '今天' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jīntiān', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '明天' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('míngtiān', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '年' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nián', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '下雨' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xiàyǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '喝水' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hē shuǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '做什么' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zuò shénme', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '去哪儿' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qù nǎr', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '我' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('wǒ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '和' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hé', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '你' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '对吗' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('duì ma', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '妹妹' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('mèimei', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '他们' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('tāmen', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '姐姐' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jiějie', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '儿子' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('érzi', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '衣服' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yīfu', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '请' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qǐng', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '喝' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hē', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '茶' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('chá', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '在' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zài', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '商店' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shāngdiàn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '里' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('lǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '三' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('sān', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '本' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('běn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '书' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shū', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '很' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hěn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '漂亮' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('piàoliang', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '他' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('tā', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '睡觉' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shuìjiào', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '呢' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('ne', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '太' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('tài', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '热' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('rè', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '了' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('le', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '是' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shì', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '有' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yǒu', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '不' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('bù', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '好' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hǎo', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '人' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('rén', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '家' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jiā', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '学校' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xuéxiào', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '老师' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('lǎoshī', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '学生' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xuésheng', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '朋友' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('péngyou', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '早上' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zǎoshang', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '中午' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zhōngwǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '晚上' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('wǎnshang', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '一' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yī', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '二' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('èr', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '四' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('sì', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '五' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('wǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '六' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('liù', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '七' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qī', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '八' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('bā', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '九' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jiǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '十' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shí', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '点' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('diǎn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '分钟' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('fēnzhōng', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '来' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('lái', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '回' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('huí', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '看' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('kàn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '电视' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('diànshì', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '工作' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('gōngzuò', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '会' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('huì', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '爸爸' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('bàba', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '妈妈' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('māma', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '现在' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xiànzài', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '几' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('jǐ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '多少' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('duōshao', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '什么' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shénme', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '哪' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nǎ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '这' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zhè', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '那' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('nà', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '买' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('mǎi', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '钱' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('qián', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '东西' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('dōngxi', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '学习' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xuéxí', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '汉语' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('hànyǔ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '说' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shuō', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '写' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('xiě', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '字' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zì', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '医生' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yīshēng', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '医院' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('yīyuàn', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '苹果' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('píngguǒ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '水果' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('shuǐguǒ', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);
INSERT INTO user_words (user_id, word_id, status, note, learned_at, created_at, updated_at)
SELECT 1, w.id, 'LEARNED', NULL, NOW(6), NOW(6), NOW(6) FROM words w
WHERE w.simplified = '坐' AND LOWER(REPLACE(w.pinyin, ' ', '')) COLLATE utf8mb4_0900_as_cs = LOWER(REPLACE('zuò', ' ', '')) COLLATE utf8mb4_0900_as_cs
  AND NOT EXISTS (SELECT 1 FROM user_words uw WHERE uw.user_id = 1 AND uw.word_id = w.id);

-- 3. 90 cau luyen nghe co san (source BUILTIN)
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在几点？', 'Xiànzài jǐ diǎn?', 'Bây giờ mấy giờ?', 1, 'BUILTIN', '现在几点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在几点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在八点。', 'Xiànzài bā diǎn.', 'Bây giờ tám giờ.', 1, 'BUILTIN', '现在八点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在八点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这是什么？', 'Zhè shì shénme?', 'Đây là cái gì?', 1, 'BUILTIN', '这是什么', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这是什么');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '那是什么？', 'Nà shì shénme?', 'Kia là cái gì?', 1, 'BUILTIN', '那是什么', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '那是什么');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '多少钱？', 'Duōshao qián?', 'Bao nhiêu tiền?', 1, 'BUILTIN', '多少钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '多少钱');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我买东西。', 'Wǒ mǎi dōngxi.', 'Tôi mua đồ.', 1, 'BUILTIN', '我买东西', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我买东西');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你买什么？', 'Nǐ mǎi shénme?', 'Bạn mua gì?', 1, 'BUILTIN', '你买什么', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你买什么');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这是茶。', 'Zhè shì chá.', 'Đây là trà.', 1, 'BUILTIN', '这是茶', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这是茶');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '那是书。', 'Nà shì shū.', 'Kia là sách.', 1, 'BUILTIN', '那是书', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '那是书');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '哪本书？', 'Nǎ běn shū?', 'Quyển sách nào?', 1, 'BUILTIN', '哪本书', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '哪本书');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '爸爸工作。', 'Bàba gōngzuò.', 'Bố làm việc.', 1, 'BUILTIN', '爸爸工作', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '爸爸工作');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妈妈看电视。', 'Māma kàn diànshì.', 'Mẹ xem tivi.', 1, 'BUILTIN', '妈妈看电视', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妈妈看电视');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '他来了。', 'Tā lái le.', 'Anh ấy đến rồi.', 1, 'BUILTIN', '他来了', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '他来了');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我回家。', 'Wǒ huí jiā.', 'Tôi về nhà.', 1, 'BUILTIN', '我回家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我回家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我会开车。', 'Wǒ huì kāichē.', 'Tôi biết lái xe.', 1, 'BUILTIN', '我会开车', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我会开车');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '早上七点。', 'Zǎoshang qī diǎn.', 'Bảy giờ sáng.', 1, 'BUILTIN', '早上七点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '早上七点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '中午十二点。', 'Zhōngwǔ shí’èr diǎn.', 'Mười hai giờ trưa.', 1, 'BUILTIN', '中午十二点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '中午十二点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '晚上九点。', 'Wǎnshang jiǔ diǎn.', 'Chín giờ tối.', 1, 'BUILTIN', '晚上九点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '晚上九点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '五分钟。', 'Wǔ fēnzhōng.', 'Năm phút.', 1, 'BUILTIN', '五分钟', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '五分钟');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '太热了。', 'Tài rè le.', 'Nóng quá.', 1, 'BUILTIN', '太热了', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '太热了');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '今天下雨。', 'Jīntiān xiàyǔ.', 'Hôm nay trời mưa.', 1, 'BUILTIN', '今天下雨', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '今天下雨');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我很好。', 'Wǒ hěn hǎo.', 'Tôi rất khỏe.', 1, 'BUILTIN', '我很好', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我很好');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你呢？', 'Nǐ ne?', 'Còn bạn thì sao?', 1, 'BUILTIN', '你呢', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你呢');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '请喝茶。', 'Qǐng hē chá.', 'Mời uống trà.', 1, 'BUILTIN', '请喝茶', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '请喝茶');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我有钱。', 'Wǒ yǒu qián.', 'Tôi có tiền.', 1, 'BUILTIN', '我有钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我有钱');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我去商店。', 'Wǒ qù shāngdiàn.', 'Tôi đi cửa hàng.', 1, 'BUILTIN', '我去商店', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我去商店');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '儿子睡觉。', 'Érzi shuìjiào.', 'Con trai đi ngủ.', 1, 'BUILTIN', '儿子睡觉', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '儿子睡觉');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '他是老师。', 'Tā shì lǎoshī.', 'Anh ấy là giáo viên.', 1, 'BUILTIN', '他是老师', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '他是老师');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我学习汉语。', 'Wǒ xuéxí Hànyǔ.', 'Tôi học tiếng Trung.', 1, 'BUILTIN', '我学习汉语', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我学习汉语');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我写字。', 'Wǒ xiě zì.', 'Tôi viết chữ.', 1, 'BUILTIN', '我写字', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我写字');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在是几点？', 'Xiànzài shì jǐ diǎn?', 'Bây giờ là mấy giờ?', 2, 'BUILTIN', '现在是几点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在是几点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在几点了？', 'Xiànzài jǐ diǎn le?', 'Bây giờ mấy giờ rồi?', 2, 'BUILTIN', '现在几点了', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在几点了');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这东西多少钱？', 'Zhè dōngxi duōshao qián?', 'Đồ này bao nhiêu tiền?', 2, 'BUILTIN', '这东西多少钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这东西多少钱');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '那本书多少钱？', 'Nà běn shū duōshao qián?', 'Quyển sách kia bao nhiêu tiền?', 2, 'BUILTIN', '那本书多少钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '那本书多少钱');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你买什么东西？', 'Nǐ mǎi shénme dōngxi?', 'Bạn mua đồ gì?', 2, 'BUILTIN', '你买什么东西', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你买什么东西');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妈妈买东西了。', 'Māma mǎi dōngxi le.', 'Mẹ mua đồ rồi.', 2, 'BUILTIN', '妈妈买东西了', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妈妈买东西了');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '爸爸晚上回家。', 'Bàba wǎnshang huí jiā.', 'Buổi tối bố về nhà.', 2, 'BUILTIN', '爸爸晚上回家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '爸爸晚上回家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我早上喝茶。', 'Wǒ zǎoshang hē chá.', 'Buổi sáng tôi uống trà.', 2, 'BUILTIN', '我早上喝茶', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我早上喝茶');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '中午我吃饭。', 'Zhōngwǔ wǒ chīfàn.', 'Buổi trưa tôi ăn cơm.', 2, 'BUILTIN', '中午我吃饭', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '中午我吃饭');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '晚上我看电视。', 'Wǎnshang wǒ kàn diànshì.', 'Buổi tối tôi xem tivi.', 2, 'BUILTIN', '晚上我看电视', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '晚上我看电视');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妈妈在打电话。', 'Māma zài dǎ diànhuà.', 'Mẹ đang gọi điện thoại.', 2, 'BUILTIN', '妈妈在打电话', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妈妈在打电话');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我会说汉语。', 'Wǒ huì shuō Hànyǔ.', 'Tôi biết nói tiếng Trung.', 2, 'BUILTIN', '我会说汉语', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我会说汉语');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '他不会写字。', 'Tā bú huì xiě zì.', 'Anh ấy không biết viết chữ.', 2, 'BUILTIN', '他不会写字', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '他不会写字');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '姐姐是医生。', 'Jiějie shì yīshēng.', 'Chị gái là bác sĩ.', 2, 'BUILTIN', '姐姐是医生', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '姐姐是医生');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我有三本书。', 'Wǒ yǒu sān běn shū.', 'Tôi có ba quyển sách.', 2, 'BUILTIN', '我有三本书', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我有三本书');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你有几本书？', 'Nǐ yǒu jǐ běn shū?', 'Bạn có mấy quyển sách?', 2, 'BUILTIN', '你有几本书', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你有几本书');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '家里有人。', 'Jiā lǐ yǒu rén.', 'Trong nhà có người.', 2, 'BUILTIN', '家里有人', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '家里有人');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '商店里有水果。', 'Shāngdiàn lǐ yǒu shuǐguǒ.', 'Trong cửa hàng có hoa quả.', 2, 'BUILTIN', '商店里有水果', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '商店里有水果');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这衣服很漂亮。', 'Zhè yīfu hěn piàoliang.', 'Bộ quần áo này rất đẹp.', 2, 'BUILTIN', '这衣服很漂亮', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这衣服很漂亮');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这本书太好了。', 'Zhè běn shū tài hǎo le.', 'Quyển sách này hay quá.', 2, 'BUILTIN', '这本书太好了', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这本书太好了');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '今天太热了。', 'Jīntiān tài rè le.', 'Hôm nay nóng quá.', 2, 'BUILTIN', '今天太热了', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '今天太热了');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '昨天我在家。', 'Zuótiān wǒ zài jiā.', 'Hôm qua tôi ở nhà.', 2, 'BUILTIN', '昨天我在家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '昨天我在家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '明天他来我家。', 'Míngtiān tā lái wǒ jiā.', 'Ngày mai anh ấy đến nhà tôi.', 2, 'BUILTIN', '明天他来我家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '明天他来我家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我坐车回家。', 'Wǒ zuò chē huí jiā.', 'Tôi đi xe về nhà.', 2, 'BUILTIN', '我坐车回家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我坐车回家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '老师在写字。', 'Lǎoshī zài xiě zì.', 'Giáo viên đang viết chữ.', 2, 'BUILTIN', '老师在写字', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '老师在写字');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '学生在学校。', 'Xuésheng zài xuéxiào.', 'Học sinh ở trường.', 2, 'BUILTIN', '学生在学校', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '学生在学校');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你去哪儿？', 'Nǐ qù nǎr?', 'Bạn đi đâu?', 2, 'BUILTIN', '你去哪儿', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你去哪儿');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你在做什么？', 'Nǐ zài zuò shénme?', 'Bạn đang làm gì?', 2, 'BUILTIN', '你在做什么', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你在做什么');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '他们在吃饭。', 'Tāmen zài chīfàn.', 'Họ đang ăn cơm.', 2, 'BUILTIN', '他们在吃饭', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '他们在吃饭');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '苹果多少钱？', 'Píngguǒ duōshao qián?', 'Táo bao nhiêu tiền?', 2, 'BUILTIN', '苹果多少钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '苹果多少钱');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在是晚上九点。', 'Xiànzài shì wǎnshang jiǔ diǎn.', 'Bây giờ là chín giờ tối.', 3, 'BUILTIN', '现在是晚上九点', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在是晚上九点');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我早上七点吃饭。', 'Wǒ zǎoshang qī diǎn chīfàn.', 'Bảy giờ sáng tôi ăn cơm.', 3, 'BUILTIN', '我早上七点吃饭', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我早上七点吃饭');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妈妈中午十二点回家。', 'Māma zhōngwǔ shí’èr diǎn huí jiā.', 'Mười hai giờ trưa mẹ về nhà.', 3, 'BUILTIN', '妈妈中午十二点回家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妈妈中午十二点回家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '爸爸晚上八点看电视。', 'Bàba wǎnshang bā diǎn kàn diànshì.', 'Tám giờ tối bố xem tivi.', 3, 'BUILTIN', '爸爸晚上八点看电视', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '爸爸晚上八点看电视');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我晚上十点睡觉。', 'Wǒ wǎnshang shí diǎn shuìjiào.', 'Mười giờ tối tôi đi ngủ.', 3, 'BUILTIN', '我晚上十点睡觉', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我晚上十点睡觉');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '老师九点来学校。', 'Lǎoshī jiǔ diǎn lái xuéxiào.', 'Chín giờ giáo viên đến trường.', 3, 'BUILTIN', '老师九点来学校', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '老师九点来学校');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '他们三点去商店买东西。', 'Tāmen sān diǎn qù shāngdiàn mǎi dōngxi.', 'Ba giờ họ đi cửa hàng mua đồ.', 3, 'BUILTIN', '他们三点去商店买东西', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '他们三点去商店买东西');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妈妈买了衣服和书。', 'Māma mǎi le yīfu hé shū.', 'Mẹ đã mua quần áo và sách.', 3, 'BUILTIN', '妈妈买了衣服和书', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妈妈买了衣服和书');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我在商店买水果。', 'Wǒ zài shāngdiàn mǎi shuǐguǒ.', 'Tôi mua hoa quả ở cửa hàng.', 3, 'BUILTIN', '我在商店买水果', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我在商店买水果');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在商店里有人。', 'Xiànzài shāngdiàn lǐ yǒu rén.', 'Bây giờ trong cửa hàng có người.', 3, 'BUILTIN', '现在商店里有人', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在商店里有人');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你今天买什么东西？', 'Nǐ jīntiān mǎi shénme dōngxi?', 'Hôm nay bạn mua đồ gì?', 3, 'BUILTIN', '你今天买什么东西', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你今天买什么东西');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这本书和那本书多少钱？', 'Zhè běn shū hé nà běn shū duōshao qián?', 'Quyển sách này và quyển kia bao nhiêu tiền?', 3, 'BUILTIN', '这本书和那本书多少钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这本书和那本书多少钱');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '爸爸会开车，妈妈不会。', 'Bàba huì kāichē, māma bú huì.', 'Bố biết lái xe, mẹ thì không.', 3, 'BUILTIN', '爸爸会开车妈妈不会', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '爸爸会开车妈妈不会');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我会说汉语，他不会。', 'Wǒ huì shuō Hànyǔ, tā bú huì.', 'Tôi biết nói tiếng Trung, anh ấy thì không.', 3, 'BUILTIN', '我会说汉语他不会', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我会说汉语他不会');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '姐姐在医院工作。', 'Jiějie zài yīyuàn gōngzuò.', 'Chị gái làm việc ở bệnh viện.', 3, 'BUILTIN', '姐姐在医院工作', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '姐姐在医院工作');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妹妹在学校学习汉语。', 'Mèimei zài xuéxiào xuéxí Hànyǔ.', 'Em gái học tiếng Trung ở trường.', 3, 'BUILTIN', '妹妹在学校学习汉语', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妹妹在学校学习汉语');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我儿子是学生。', 'Wǒ érzi shì xuésheng.', 'Con trai tôi là học sinh.', 3, 'BUILTIN', '我儿子是学生', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我儿子是学生');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '明天下雨，我不去学校。', 'Míngtiān xiàyǔ, wǒ bú qù xuéxiào.', 'Ngày mai trời mưa, tôi không đi học.', 3, 'BUILTIN', '明天下雨我不去学校', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '明天下雨我不去学校');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '昨天太热了，我在家睡觉。', 'Zuótiān tài rè le, wǒ zài jiā shuìjiào.', 'Hôm qua nóng quá, tôi ở nhà ngủ.', 3, 'BUILTIN', '昨天太热了我在家睡觉', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '昨天太热了我在家睡觉');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你现在做什么呢？', 'Nǐ xiànzài zuò shénme ne?', 'Bây giờ bạn đang làm gì thế?', 3, 'BUILTIN', '你现在做什么呢', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你现在做什么呢');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '你是老师，对吗？', 'Nǐ shì lǎoshī, duì ma?', 'Bạn là giáo viên, đúng không?', 3, 'BUILTIN', '你是老师对吗', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '你是老师对吗');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '那是你妹妹，对吗？', 'Nà shì nǐ mèimei, duì ma?', 'Kia là em gái bạn, đúng không?', 3, 'BUILTIN', '那是你妹妹对吗', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '那是你妹妹对吗');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '他们明天来我家吃饭。', 'Tāmen míngtiān lái wǒ jiā chīfàn.', 'Ngày mai họ đến nhà tôi ăn cơm.', 3, 'BUILTIN', '他们明天来我家吃饭', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '他们明天来我家吃饭');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我朋友会说汉语。', 'Wǒ péngyou huì shuō Hànyǔ.', 'Bạn tôi biết nói tiếng Trung.', 3, 'BUILTIN', '我朋友会说汉语', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我朋友会说汉语');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '老师在学校写字。', 'Lǎoshī zài xuéxiào xiě zì.', 'Giáo viên viết chữ ở trường.', 3, 'BUILTIN', '老师在学校写字', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '老师在学校写字');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我看了十分钟电视。', 'Wǒ kàn le shí fēnzhōng diànshì.', 'Tôi đã xem tivi mười phút.', 3, 'BUILTIN', '我看了十分钟电视', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我看了十分钟电视');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '我在家看电视，你呢？', 'Wǒ zài jiā kàn diànshì, nǐ ne?', 'Tôi ở nhà xem tivi, còn bạn?', 3, 'BUILTIN', '我在家看电视你呢', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '我在家看电视你呢');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '妈妈买了三本书。', 'Māma mǎi le sān běn shū.', 'Mẹ đã mua ba quyển sách.', 3, 'BUILTIN', '妈妈买了三本书', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '妈妈买了三本书');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '现在是五点，我回家。', 'Xiànzài shì wǔ diǎn, wǒ huí jiā.', 'Bây giờ là năm giờ, tôi về nhà.', 3, 'BUILTIN', '现在是五点我回家', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '现在是五点我回家');
INSERT INTO user_sentences (user_id, hanzi, pinyin, meaning_vi, level, source, hanzi_key, created_at)
SELECT 1, '这苹果很好，多少钱？', 'Zhè píngguǒ hěn hǎo, duōshao qián?', 'Táo này ngon lắm, bao nhiêu tiền?', 3, 'BUILTIN', '这苹果很好多少钱', NOW(6) FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM user_sentences s WHERE s.user_id = 1 AND s.hanzi_key = '这苹果很好多少钱');
