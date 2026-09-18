-- =====================================================================
-- V4 : Seed tu vung HSK 1 - PHAN 2 (words id 76..150)
-- Noi tiep V3 (id 1..75: dai tu, so dem, luong tu, chao hoi, xung ho,
-- gia dinh, tu chi thoi gian, cac dong tu co ban nhat). File nay bo sung
-- 75 tu CON LAI cua danh sach 150 tu HSK 1 chinh thuc, KHONG lap lai bat
-- ky tu nao da co trong V3 (uk_words_simplified_pinyin se chan import):
--   - dong tu hang ngay (吃, 喝, 看, 听, 读, 写, 想, 做, 睡觉, 看见, 打电话)
--   - tu de hoi (怎么, 怎么样) va tro tu (吗, 呢, 的, 了)
--   - tinh tu / pho tu thong dung (多, 少, 大, 小, 冷, 热, 漂亮, 很, 太, 都, 不)
--   - dong tu nang nguyen (会, 能) va phu dinh 没有
--   - tu chi vi tri (上, 下, 里, 前面, 后面)
--   - thoi tiet (天气, 下雨), an uong (水, 菜, 茶, 米饭, 水果, 苹果, 杯子, 饭店)
--   - mua sam (买, 钱, 商店, 东西), giao thong (出租车, 飞机, 开, 坐)
--   - nghe nghiep, truong hoc, do vat trong nha, dong vat, dia danh
--
-- Moi tu gom: 1 dong words + 1 dong word_examples + 1..2 dong word_topics.
-- hsk_level = 1, frequency_rank = id, timestamp chuan '2026-01-01 00:00:00.000000'.
-- Quy uoc topic_id - PHAI khop voi topics da seed o V2 (id 1..14):
--   1 chao hoi, 2 gia dinh, 3 so dem, 4 thoi gian, 5 mau sac,
--   6 do an va thuc uong, 7 hoc tap, 8 cong viec, 9 mua sam,
--   10 giao thong, 11 thoi tiet, 12 co the va suc khoe, 13 nha cua,
--   14 dong tu thong dung
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. WORDS 76..150
-- ---------------------------------------------------------------------

-- 76..85 : dong tu co ban hang ngay va tu chi so luong
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(76,  '吃',     '吃',     'chī',       'chi1',           'ăn',                                                'to eat',                                   'động từ',         1, 6,  76,  'Tân ngữ thường gặp: 吃饭, 吃菜, 吃苹果.',                                     '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(77,  '喝',     '喝',     'hē',        'he1',            'uống',                                              'to drink',                                 'động từ',         1, 12, 77,  'Tân ngữ thường gặp: 喝水, 喝茶.',                                            '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(78,  '看',     '看',     'kàn',       'kan4',           'nhìn, xem; đọc',                                    'to look, to watch; to read',               'động từ',         1, 9,  78,  '看书 là đọc sách, 看电视 là xem tivi, 看电影 là xem phim.',                    '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(79,  '听',     '聽',     'tīng',      'ting1',          'nghe',                                              'to listen, to hear',                       'động từ',         1, 7,  79,  NULL,                                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(80,  '读',     '讀',     'dú',        'du2',            'đọc, đọc thành tiếng',                              'to read, to read aloud',                   'động từ',         1, 10, 80,  'Nhấn mạnh việc đọc thành tiếng, khác với 看书 là đọc thầm.',                  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(81,  '写',     '寫',     'xiě',       'xie3',           'viết',                                              'to write',                                 'động từ',         1, 5,  81,  '写字 là viết chữ.',                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(82,  '多',     '多',     'duō',       'duo1',           'nhiều',                                             'many, much',                               'tính từ',         1, 6,  82,  NULL,                                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(83,  '少',     '少',     'shǎo',      'shao3',          'ít',                                                'few, little',                              'tính từ',         1, 4,  83,  NULL,                                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(84,  '想',     '想',     'xiǎng',     'xiang3',         'muốn; nghĩ; nhớ (ai đó)',                           'to want; to think; to miss',               'động từ',         1, 13, 84,  'Đứng trước động từ mang nghĩa muốn làm gì: 想去, 想喝水.',                     '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(85,  '一点儿', '一點兒', 'yìdiǎnr',   'yi4 dian3 r5',   'một chút, một ít',                                  'a little, a bit',                          'lượng từ',        1, 12, 85,  'Đứng trước danh từ để chỉ số lượng ít: 一点儿水.',                            '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 86..96 : tu de hoi, tro tu, lien tu va pho tu
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(86,  '怎么',   '怎麼',   'zěnme',     'zen3 me5',       'thế nào, làm sao, tại sao',                         'how, why',                                 'đại từ nghi vấn', 1, 12, 86,  'Hỏi về cách thức: 怎么走? 怎么读?',                                            '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(87,  '怎么样', '怎麼樣', 'zěnmeyàng', 'zen3 me5 yang4', 'thế nào, ra sao',                                   'how about, how is it',                     'đại từ nghi vấn', 1, 22, 87,  'Hỏi về tình trạng hoặc ý kiến: 天气怎么样？',                                  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(88,  '吗',     '嗎',     'ma',        'ma5',            'không? phải không? (trợ từ tạo câu hỏi)',           'yes-no question particle',                 'trợ từ ngữ khí',  1, 6,  88,  'Đặt cuối câu trần thuật để tạo câu hỏi đúng hay sai.',                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(89,  '呢',     '呢',     'ne',        'ne5',            'thì sao? còn ... thì sao? (trợ từ nghi vấn)',       'particle for follow-up question',           'trợ từ ngữ khí',  1, 8,  89,  'Dùng cuối câu để hỏi lại người khác: 你呢？',                                  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(90,  '的',     '的',     'de',        'de5',            'của (trợ từ kết cấu nối định ngữ)',                 'possessive and attributive particle',      'trợ từ kết cấu',  1, 8,  90,  'Nối định ngữ với danh từ trung tâm: 我的书, 漂亮的衣服.',                      '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(91,  '了',     '了',     'le',        'le5',            'rồi (trợ từ chỉ hành động hoàn thành)',             'particle marking completed action',        'trợ từ',          1, 2,  91,  'Đặt sau động từ hoặc cuối câu để chỉ việc đã xảy ra.',                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(92,  '和',     '和',     'hé',        'he2',            'và, với, cùng',                                     'and, with',                                'liên từ',         1, 8,  92,  'Nối hai danh từ, không dùng để nối hai mệnh đề.',                            '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(93,  '都',     '都',     'dōu',       'dou1',           'đều, tất cả',                                       'all, both',                                'phó từ',          1, 10, 93,  'Đứng sau chủ ngữ và trước động từ: 我们都是学生.',                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(94,  '很',     '很',     'hěn',       'hen3',           'rất',                                               'very',                                     'phó từ',          1, 9,  94,  'Thường bắt buộc có trước tính từ vị ngữ, nhiều khi không mang nghĩa mạnh.',  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(95,  '太',     '太',     'tài',       'tai4',           'quá, quá là',                                       'too, excessively',                         'phó từ',          1, 4,  95,  'Cấu trúc thường gặp: 太 + tính từ + 了.',                                     '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(96,  '不',     '不',     'bù',        'bu4',            'không (dùng để phủ định)',                          'not, no',                                  'phó từ',          1, 4,  96,  'Phủ định hiện tại và tương lai; phủ định của 有 phải dùng 没.',                '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 97..108 : tinh tu mieu ta, dong tu nang nguyen va tu chi vi tri
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(97,  '大',     '大',     'dà',        'da4',            'to, lớn',                                           'big, large',                               'tính từ',             1, 3,  97,  NULL,                                                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(98,  '小',     '小',     'xiǎo',      'xiao3',          'nhỏ, bé',                                           'small, little',                            'tính từ',             1, 3,  98,  NULL,                                                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(99,  '冷',     '冷',     'lěng',      'leng3',          'lạnh',                                              'cold',                                     'tính từ',             1, 7,  99,  NULL,                                                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(100, '热',     '熱',     'rè',        're4',            'nóng',                                              'hot',                                      'tính từ',             1, 10, 100, 'Dùng cho thời tiết và cho đồ ăn thức uống: 天气很热, 热茶.', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(101, '会',     '會',     'huì',       'hui4',           'biết (làm gì đó); sẽ',                              'can, to know how to; will',                'động từ năng nguyện', 1, 6,  101, 'Chỉ khả năng có được nhờ học tập: 我会说汉语.',              '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(102, '漂亮',   '漂亮',   'piàoliang', 'piao4 liang5',   'đẹp, xinh đẹp',                                     'pretty, beautiful',                        'tính từ',             1, 23, 102, NULL,                                                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(103, '上',     '上',     'shàng',     'shang4',         'trên, phía trên; lên',                              'up, above, on',                            'danh từ vị trí',      1, 3,  103, 'Danh từ vị trí đặt sau danh từ: 桌子上.',                   '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(104, '下',     '下',     'xià',       'xia4',           'dưới, phía dưới; xuống',                            'down, below, under',                       'danh từ vị trí',      1, 3,  104, 'Danh từ vị trí đặt sau danh từ: 椅子下.',                   '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(105, '里',     '裡',     'lǐ',        'li3',            'trong, bên trong',                                  'inside, in',                               'danh từ vị trí',      1, 7,  105, 'Đặt sau danh từ chỉ nơi chốn: 学校里, 商店里.',             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(106, '前面',   '前面',   'qiánmiàn',  'qian2 mian4',    'phía trước, đằng trước',                            'in front, ahead',                          'danh từ vị trí',      1, 18, 106, NULL,                                                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(107, '后面',   '後面',   'hòumiàn',   'hou4 mian4',     'phía sau, đằng sau',                                'behind, at the back',                      'danh từ vị trí',      1, 15, 107, NULL,                                                       '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(108, '能',     '能',     'néng',      'neng2',          'có thể, có khả năng',                               'can, to be able to',                       'động từ năng nguyện', 1, 10, 108, 'Nhấn mạnh điều kiện hoặc năng lực: 你能来吗？',             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 109..118 : thoi tiet, do an va do uong
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(109, '天气',   '天氣',   'tiānqì',    'tian1 qi4',      'thời tiết',                                         'weather',                                  'danh từ',      1, 8,  109, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(110, '下雨',   '下雨',   'xià yǔ',    'xia4 yu3',       'mưa, trời mưa',                                     'to rain',                                  'cụm động từ',  1, 11, 110, 'Là cụm động tân, tự nó đã đủ nghĩa, không cần chủ ngữ.',      '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(111, '水',     '水',     'shuǐ',      'shui3',          'nước',                                              'water',                                    'danh từ',      1, 4,  111, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(112, '菜',     '菜',     'cài',       'cai4',           'món ăn; rau',                                       'dish, food, vegetable',                    'danh từ',      1, 11, 112, 'Vừa chỉ rau, vừa chỉ món ăn nói chung: 中国菜.',              '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(113, '茶',     '茶',     'chá',       'cha2',           'trà, chè',                                          'tea',                                      'danh từ',      1, 9,  113, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(114, '米饭',   '米飯',   'mǐfàn',     'mi3 fan4',       'cơm',                                               'cooked rice',                              'danh từ',      1, 13, 114, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(115, '水果',   '水果',   'shuǐguǒ',   'shui3 guo3',     'trái cây, hoa quả',                                 'fruit',                                    'danh từ',      1, 12, 115, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(116, '苹果',   '蘋果',   'píngguǒ',   'ping2 guo3',     'quả táo',                                           'apple',                                    'danh từ',      1, 16, 116, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(117, '杯子',   '杯子',   'bēizi',     'bei1 zi5',       'cái cốc, cái ly',                                   'cup, glass',                               'danh từ',      1, 11, 117, NULL,                                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(118, '饭店',   '飯店',   'fàndiàn',   'fan4 dian4',     'nhà hàng, quán ăn; khách sạn',                      'restaurant, hotel',                        'danh từ',      1, 15, 118, 'Ở Trung Quốc 饭店 vừa có nghĩa nhà hàng vừa có nghĩa khách sạn.', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 119..127 : mua sam va giao thong di lai
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(119, '买',     '買',     'mǎi',       'mai3',           'mua',                                               'to buy',                                   'động từ', 1, 6,  119, NULL,                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(120, '钱',     '錢',     'qián',      'qian2',          'tiền',                                              'money',                                    'danh từ', 1, 10, 120, 'Hỏi giá: 多少钱？',                                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(121, '商店',   '商店',   'shāngdiàn', 'shang1 dian4',   'cửa hàng, cửa hiệu',                                'shop, store',                              'danh từ', 1, 19, 121, NULL,                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(122, '东西',   '東西',   'dōngxi',    'dong1 xi5',      'đồ vật, đồ đạc, thứ',                               'thing, stuff',                             'danh từ', 1, 11, 122, 'Đọc dōngxi nghĩa là đồ vật, khác với dōngxī nghĩa đông tây.', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(123, '出租车', '出租車', 'chūzūchē',  'chu1 zu1 che1',  'xe taxi',                                           'taxi',                                     'danh từ', 1, 19, 123, NULL,                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(124, '飞机',   '飛機',   'fēijī',     'fei1 ji1',       'máy bay',                                           'airplane',                                 'danh từ', 1, 9,  124, NULL,                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(125, '开',     '開',     'kāi',       'kai1',           'mở; lái (xe); bật',                                 'to open; to drive; to turn on',            'động từ', 1, 4,  125, '开车 là lái xe, 开门 là mở cửa.',                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(126, '坐',     '坐',     'zuò',       'zuo4',           'ngồi; đi (bằng phương tiện)',                       'to sit; to travel by',                     'động từ', 1, 7,  126, '坐 + phương tiện có nghĩa đi bằng phương tiện đó: 坐飞机.',   '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(127, '医院',   '醫院',   'yīyuàn',    'yi1 yuan4',      'bệnh viện',                                         'hospital',                                 'danh từ', 1, 16, 127, NULL,                                                        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 128..135 : cong viec, sinh hoat hang ngay, dia danh va nghe nghiep
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(128, '工作',   '工作',   'gōngzuò',   'gong1 zuo4',     'công việc; làm việc',                               'work, job; to work',                       'danh từ / động từ', 1, 10, 128, 'Vừa là danh từ vừa là động từ: 找工作, 在医院工作.',             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(129, '做',     '做',     'zuò',       'zuo4',           'làm, chế tạo; nấu',                                 'to do, to make; to cook',                  'động từ',           1, 11, 129, '做饭 là nấu cơm, 做菜 là nấu món ăn.',                          '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(130, '打电话', '打電話', 'dǎ diànhuà', 'da3 dian4 hua4', 'gọi điện thoại',                                    'to make a phone call',                     'cụm động từ',       1, 18, 130, 'Là cụm động tân, có thể tách ra: 打一个电话.',                  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(131, '睡觉',   '睡覺',   'shuìjiào',  'shui4 jiao4',    'ngủ, đi ngủ',                                       'to sleep, to go to bed',                   'cụm động từ',       1, 22, 131, 'Là cụm động tân, tự nó đã đủ nghĩa, không thêm tân ngữ khác.',  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(132, '看见',   '看見',   'kànjiàn',   'kan4 jian4',     'nhìn thấy, trông thấy',                             'to see, to catch sight of',                'động từ',           1, 13, 132, '见 là bổ ngữ kết quả của 看: nhìn và thấy được.',               '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(133, '没有',   '沒有',   'méiyǒu',    'mei2 you3',      'không có; chưa (phủ định của 有)',                  'to not have; there is not',                'động từ',           1, 13, 133, 'Phủ định của 有 luôn dùng 没, tuyệt đối không dùng 不.',        '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(134, '中国',   '中國',   'Zhōngguó',  'zhong1 guo2',    'Trung Quốc',                                        'China',                                    'danh từ riêng',     1, 12, 134, '中国人 là người Trung Quốc, 中国菜 là món ăn Trung Quốc.',      '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(135, '医生',   '醫生',   'yīshēng',   'yi1 sheng1',     'bác sĩ',                                            'doctor',                                   'danh từ',           1, 12, 135, NULL,                                                           '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 136..141 : truong hoc, hoc tap
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(136, '学校',   '學校',   'xuéxiào',   'xue2 xiao4',     'trường học',                                        'school',                                   'danh từ', 1, 18, 136, NULL,                                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(137, '书',     '書',     'shū',       'shu1',           'sách, quyển sách',                                  'book',                                     'danh từ', 1, 4,  137, 'Lượng từ đi kèm là 本: 一本书.',                                  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(138, '字',     '字',     'zì',        'zi4',            'chữ, chữ Hán',                                      'character, written word',                  'danh từ', 1, 6,  138, NULL,                                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(139, '汉语',   '漢語',   'Hànyǔ',     'han4 yu3',       'tiếng Hán, tiếng Trung Quốc',                       'Chinese language',                         'danh từ', 1, 14, 139, 'Còn được gọi là 中文.',                                           '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(140, '学习',   '學習',   'xuéxí',     'xue2 xi2',       'học, học tập',                                      'to study, to learn',                       'động từ', 1, 11, 140, 'Có thể dùng một mình hoặc mang tân ngữ: 学习汉语.',               '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(141, '喜欢',   '喜歡',   'xǐhuan',    'xi3 huan5',      'thích, yêu thích',                                  'to like, to be fond of',                   'động từ', 1, 18, 141, 'Phía sau có thể là danh từ hoặc động từ: 喜欢喝茶.',              '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- 142..150 : do vat trong nha, giai tri, dong vat va dia danh
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, note, created_at, updated_at) VALUES
(142, '桌子',   '桌子',   'zhuōzi',    'zhuo1 zi5',      'cái bàn',                                           'table, desk',                              'danh từ',       1, 13, 142, NULL,                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(143, '椅子',   '椅子',   'yǐzi',      'yi3 zi5',        'cái ghế',                                           'chair',                                    'danh từ',       1, 15, 143, NULL,                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(144, '衣服',   '衣服',   'yīfu',      'yi1 fu5',        'quần áo',                                           'clothes',                                  'danh từ',       1, 14, 144, NULL,                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(145, '电脑',   '電腦',   'diànnǎo',   'dian4 nao3',     'máy vi tính, máy tính',                             'computer',                                 'danh từ',       1, 15, 145, NULL,                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(146, '电视',   '電視',   'diànshì',   'dian4 shi4',     'tivi, truyền hình',                                 'television, TV',                           'danh từ',       1, 13, 146, 'Xem tivi là 看电视.',                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(147, '电影',   '電影',   'diànyǐng',  'dian4 ying3',    'phim, điện ảnh',                                    'movie, film',                              'danh từ',       1, 20, 147, 'Xem phim là 看电影.',                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(148, '猫',     '貓',     'māo',       'mao1',           'con mèo',                                           'cat',                                      'danh từ',       1, 11, 148, NULL,                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(149, '狗',     '狗',     'gǒu',       'gou3',           'con chó',                                           'dog',                                      'danh từ',       1, 8,  149, NULL,                                             '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(150, '北京',   '北京',   'Běijīng',   'bei3 jing1',     'Bắc Kinh (thủ đô Trung Quốc)',                      'Beijing',                                  'danh từ riêng', 1, 13, 150, 'Tên riêng nên viết hoa chữ cái đầu khi phiên âm: Běijīng.', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- ---------------------------------------------------------------------
-- 2. WORD_EXAMPLES - moi tu mot cau vi du
-- ---------------------------------------------------------------------
INSERT INTO word_examples (word_id, sentence_zh, sentence_pinyin, sentence_vi, sort_order) VALUES
(76,  '我喜欢吃中国菜。',       'Wǒ xǐhuan chī Zhōngguó cài.',         'Tôi thích ăn món ăn Trung Quốc.',            0),
(77,  '我想喝一杯茶。',         'Wǒ xiǎng hē yì bēi chá.',             'Tôi muốn uống một tách trà.',                0),
(78,  '你看，那是我的学校。',   'Nǐ kàn, nà shì wǒ de xuéxiào.',       'Bạn nhìn xem, đó là trường của tôi.',        0),
(79,  '我喜欢听老师读汉语。',   'Wǒ xǐhuan tīng lǎoshī dú Hànyǔ.',     'Tôi thích nghe cô giáo đọc tiếng Trung.',    0),
(80,  '我在读一本书。',         'Wǒ zài dú yì běn shū.',               'Tôi đang đọc một quyển sách.',               0),
(81,  '他在写字。',             'Tā zài xiě zì.',                      'Anh ấy đang viết chữ.',                      0),
(82,  '今天人很多。',           'Jīntiān rén hěn duō.',                'Hôm nay rất đông người.',                    0),
(83,  '商店里的人很少。',       'Shāngdiàn lǐ de rén hěn shǎo.',       'Trong cửa hàng rất ít người.',               0),
(84,  '妈妈想吃苹果。',         'Māma xiǎng chī píngguǒ.',             'Mẹ muốn ăn táo.',                            0),
(85,  '我会说一点儿汉语。',     'Wǒ huì shuō yìdiǎnr Hànyǔ.',          'Tôi biết nói một chút tiếng Trung.',         0),
(86,  '这个字怎么读？',         'Zhège zì zěnme dú?',                  'Chữ này đọc thế nào?',                       0),
(87,  '今天天气怎么样？',       'Jīntiān tiānqì zěnmeyàng?',           'Thời tiết hôm nay thế nào?',                 0),
(88,  '你是学生吗？',           'Nǐ shì xuésheng ma?',                 'Bạn là học sinh phải không?',                0),
(89,  '我很好，你呢？',         'Wǒ hěn hǎo, nǐ ne?',                  'Tôi khỏe, còn bạn thì sao?',                 0),
(90,  '这是老师的书。',         'Zhè shì lǎoshī de shū.',              'Đây là sách của cô giáo.',                   0),
(91,  '我吃了三个苹果。',       'Wǒ chī le sān gè píngguǒ.',           'Tôi đã ăn ba quả táo.',                      0),
(92,  '我和朋友去商店。',       'Wǒ hé péngyou qù shāngdiàn.',         'Tôi và bạn đi đến cửa hàng.',                0),
(93,  '我们都是学生。',         'Wǒmen dōu shì xuésheng.',             'Chúng tôi đều là học sinh.',                 0),
(94,  '今天很热。',             'Jīntiān hěn rè.',                     'Hôm nay rất nóng.',                          0),
(95,  '今天太冷了。',           'Jīntiān tài lěng le.',                'Hôm nay lạnh quá.',                          0),
(96,  '我不喜欢喝茶。',         'Wǒ bù xǐhuan hē chá.',                'Tôi không thích uống trà.',                  0),
(97,  '他们的学校很大。',       'Tāmen de xuéxiào hěn dà.',            'Trường học của họ rất lớn.',                 0),
(98,  '我的猫很小。',           'Wǒ de māo hěn xiǎo.',                 'Con mèo của tôi rất nhỏ.',                   0),
(99,  '下雨的时候很冷。',       'Xià yǔ de shíhou hěn lěng.',          'Lúc trời mưa thì rất lạnh.',                 0),
(100, '我想喝一点儿热茶。',     'Wǒ xiǎng hē yìdiǎnr rè chá.',         'Tôi muốn uống một chút trà nóng.',           0),
(101, '我会说汉语。',           'Wǒ huì shuō Hànyǔ.',                  'Tôi biết nói tiếng Trung.',                  0),
(102, '她的衣服很漂亮。',       'Tā de yīfu hěn piàoliang.',           'Quần áo của cô ấy rất đẹp.',                 0),
(103, '书在桌子上。',           'Shū zài zhuōzi shàng.',               'Quyển sách ở trên bàn.',                     0),
(104, '猫在椅子下。',           'Māo zài yǐzi xià.',                   'Con mèo ở dưới ghế.',                        0),
(105, '医院里人很多。',         'Yīyuàn lǐ rén hěn duō.',              'Trong bệnh viện có rất nhiều người.',        0),
(106, '学校前面有一个商店。',   'Xuéxiào qiánmiàn yǒu yí gè shāngdiàn.', 'Phía trước trường học có một cửa hàng.',   0),
(107, '我家后面有一个医院。',   'Wǒ jiā hòumiàn yǒu yí gè yīyuàn.',    'Phía sau nhà tôi có một bệnh viện.',         0),
(108, '明天你能来吗？',         'Míngtiān nǐ néng lái ma?',            'Ngày mai bạn có thể đến không?',             0),
(109, '今天的天气很好。',       'Jīntiān de tiānqì hěn hǎo.',          'Thời tiết hôm nay rất đẹp.',                 0),
(110, '明天会下雨吗？',         'Míngtiān huì xià yǔ ma?',             'Ngày mai trời sẽ mưa không?',                0),
(111, '我想喝水。',             'Wǒ xiǎng hē shuǐ.',                   'Tôi muốn uống nước.',                        0),
(112, '妈妈买了很多菜。',       'Māma mǎi le hěn duō cài.',            'Mẹ đã mua rất nhiều thức ăn.',               0),
(113, '我喜欢喝茶。',           'Wǒ xǐhuan hē chá.',                   'Tôi thích uống trà.',                        0),
(114, '我中午吃了一些米饭。',   'Wǒ zhōngwǔ chī le yìxiē mǐfàn.',      'Buổi trưa tôi đã ăn một ít cơm.',            0),
(115, '商店里有很多水果。',     'Shāngdiàn lǐ yǒu hěn duō shuǐguǒ.',   'Trong cửa hàng có rất nhiều trái cây.',      0),
(116, '这些苹果很大。',         'Zhèxiē píngguǒ hěn dà.',              'Những quả táo này rất to.',                  0),
(117, '桌子上有三个杯子。',     'Zhuōzi shàng yǒu sān gè bēizi.',      'Trên bàn có ba cái cốc.',                    0),
(118, '我们在饭店吃饭。',       'Wǒmen zài fàndiàn chī fàn.',          'Chúng tôi ăn cơm ở nhà hàng.',               0),
(119, '我想买一些水果。',       'Wǒ xiǎng mǎi yìxiē shuǐguǒ.',         'Tôi muốn mua một ít trái cây.',              0),
(120, '这些衣服多少钱？',       'Zhèxiē yīfu duōshao qián?',           'Những bộ quần áo này bao nhiêu tiền?',       0),
(121, '商店里的东西很多。',     'Shāngdiàn lǐ de dōngxi hěn duō.',     'Đồ trong cửa hàng rất nhiều.',               0),
(122, '我去商店买东西。',       'Wǒ qù shāngdiàn mǎi dōngxi.',         'Tôi đi cửa hàng mua đồ.',                    0),
(123, '我坐出租车去学校。',     'Wǒ zuò chūzūchē qù xuéxiào.',         'Tôi đi taxi đến trường.',                    0),
(124, '他明天坐飞机回家。',     'Tā míngtiān zuò fēijī huí jiā.',      'Ngày mai anh ấy đi máy bay về nhà.',         0),
(125, '老师会开车。',           'Lǎoshī huì kāi chē.',                 'Thầy giáo biết lái xe.',                     0),
(126, '他坐在椅子上。',         'Tā zuò zài yǐzi shàng.',              'Anh ấy ngồi trên ghế.',                      0),
(127, '医生在医院工作。',       'Yīshēng zài yīyuàn gōngzuò.',         'Bác sĩ làm việc ở bệnh viện.',               0),
(128, '妈妈在商店工作。',       'Māma zài shāngdiàn gōngzuò.',         'Mẹ làm việc ở cửa hàng.',                    0),
(129, '爸爸在做饭。',           'Bàba zài zuò fàn.',                   'Bố đang nấu cơm.',                           0),
(130, '我在打电话。',           'Wǒ zài dǎ diànhuà.',                  'Tôi đang gọi điện thoại.',                   0),
(131, '我十点睡觉。',           'Wǒ shí diǎn shuìjiào.',               'Tôi đi ngủ lúc mười giờ.',                   0),
(132, '我看见你的猫了。',       'Wǒ kànjiàn nǐ de māo le.',            'Tôi nhìn thấy con mèo của bạn rồi.',         0),
(133, '我没有钱。',             'Wǒ méiyǒu qián.',                     'Tôi không có tiền.',                         0),
(134, '他是中国人。',           'Tā shì Zhōngguó rén.',                'Anh ấy là người Trung Quốc.',                0),
(135, '他的妈妈是医生。',       'Tā de māma shì yīshēng.',             'Mẹ của anh ấy là bác sĩ.',                   0),
(136, '我们的学校很漂亮。',     'Wǒmen de xuéxiào hěn piàoliang.',     'Trường của chúng tôi rất đẹp.',              0),
(137, '我喜欢看书。',           'Wǒ xǐhuan kàn shū.',                  'Tôi thích đọc sách.',                        0),
(138, '这个字我不认识。',       'Zhège zì wǒ bú rènshi.',              'Chữ này tôi không biết.',                    0),
(139, '他的汉语很好。',         'Tā de Hànyǔ hěn hǎo.',                'Tiếng Trung của anh ấy rất tốt.',            0),
(140, '我在学校学习汉语。',     'Wǒ zài xuéxiào xuéxí Hànyǔ.',         'Tôi học tiếng Trung ở trường.',              0),
(141, '我很喜欢这本书。',       'Wǒ hěn xǐhuan zhè běn shū.',          'Tôi rất thích quyển sách này.',              0),
(142, '桌子上有一本书。',       'Zhuōzi shàng yǒu yì běn shū.',        'Trên bàn có một quyển sách.',                0),
(143, '这个椅子很漂亮。',       'Zhège yǐzi hěn piàoliang.',           'Cái ghế này rất đẹp.',                       0),
(144, '妈妈买了很多衣服。',     'Māma mǎi le hěn duō yīfu.',           'Mẹ đã mua rất nhiều quần áo.',               0),
(145, '我在电脑上写字。',       'Wǒ zài diànnǎo shàng xiě zì.',        'Tôi viết chữ trên máy tính.',                0),
(146, '爸爸在家看电视。',       'Bàba zài jiā kàn diànshì.',           'Bố xem tivi ở nhà.',                         0),
(147, '我和朋友去看电影。',     'Wǒ hé péngyou qù kàn diànyǐng.',      'Tôi và bạn đi xem phim.',                    0),
(148, '我很喜欢猫。',           'Wǒ hěn xǐhuan māo.',                  'Tôi rất thích mèo.',                         0),
(149, '他的狗很大。',           'Tā de gǒu hěn dà.',                   'Con chó của anh ấy rất to.',                 0),
(150, '我明天去北京。',         'Wǒ míngtiān qù Běijīng.',             'Ngày mai tôi đi Bắc Kinh.',                  0);

-- ---------------------------------------------------------------------
-- 3. WORD_TOPICS - gan moi tu vao 1..2 chu de (topic_id 1..14 cua V2)
--    1 chao hoi | 2 gia dinh | 3 so dem | 4 thoi gian | 5 mau sac
--    6 do an va thuc uong | 7 hoc tap | 8 cong viec | 9 mua sam
--    10 giao thong | 11 thoi tiet | 12 co the va suc khoe | 13 nha cua
--    14 dong tu thong dung
-- ---------------------------------------------------------------------
INSERT INTO word_topics (word_id, topic_id) VALUES
-- dong tu hang ngay va tu chi so luong
(76, 14), (76, 6),
(77, 14), (77, 6),
(78, 14), (78, 7),
(79, 14), (79, 7),
(80, 14), (80, 7),
(81, 14), (81, 7),
(82, 3),
(83, 3),
(84, 14),
(85, 3),
-- tu de hoi, tro tu, lien tu, pho tu -> giao tiep hang ngay
(86, 1),
(87, 1),
(88, 1),
(89, 1),
(90, 1),
(91, 1),
(92, 1),
(93, 1),
(94, 1),
(95, 1),
(96, 1),
-- tinh tu mieu ta, dong tu nang nguyen va tu chi vi tri
(97, 9),
(98, 9),
(99, 11),
(100, 11), (100, 6),
(101, 14),
(102, 9),
(103, 10), (103, 13),
(104, 10), (104, 13),
(105, 10), (105, 13),
(106, 10),
(107, 10),
(108, 14),
-- thoi tiet va an uong
(109, 11),
(110, 11), (110, 14),
(111, 6),
(112, 6),
(113, 6),
(114, 6),
(115, 6),  (115, 9),
(116, 6),  (116, 9),
(117, 6),  (117, 13),
(118, 6),  (118, 10),
-- mua sam va giao thong
(119, 9),  (119, 14),
(120, 9),  (120, 3),
(121, 9),  (121, 10),
(122, 9),  (122, 13),
(123, 10),
(124, 10),
(125, 10), (125, 14),
(126, 10), (126, 14),
(127, 12), (127, 10),
-- cong viec, sinh hoat hang ngay, dia danh va nghe nghiep
(128, 8),  (128, 14),
(129, 14), (129, 8),
(130, 14), (130, 1),
(131, 14), (131, 13),
(132, 14),
(133, 14),
(134, 7),
(135, 12), (135, 8),
-- truong hoc va hoc tap
(136, 7),
(137, 7),  (137, 13),
(138, 7),
(139, 7),
(140, 7),  (140, 14),
(141, 14),
-- do vat trong nha, giai tri, dong vat va dia danh
(142, 13),
(143, 13),
(144, 13), (144, 9),
(145, 13), (145, 8),
(146, 13),
(147, 13), (147, 14),
(148, 13),
(149, 13),
(150, 10);
