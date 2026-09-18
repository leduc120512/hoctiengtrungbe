-- =====================================================================
-- V3 : Seed tu vung HSK 1 - phan A (words id 1..75)
-- Noi dung : 75 tu dau tien cua danh sach HSK 1 chinh thuc,
--            sap xep theo nhom: dai tu / tu de hoi -> so tu & luong tu
--            -> chao hoi & xung ho -> gia dinh -> thoi gian -> dong tu co ban.
-- Moi tu gom : 1 dong trong words + 1 cau vi du trong word_examples
--              + 1-2 dong lien ket chu de trong word_topics.
-- Ghi chu   : pinyin co dau thanh dieu; voi cac tu 一 / 不 trong cau vi du
--             pinyin duoc viet theo thanh dieu THUC TE khi bien am
--             (vi du: yí ge, bú kèqi).
-- Chu de (topic_id) - PHAI khop dung voi bang topics seed o V2:
--   1 chao-hoi, 2 gia-dinh, 3 so-dem, 4 thoi-gian, 5 mau-sac, 6 do-an,
--   7 hoc-tap, 8 cong-viec, 9 mua-sam, 10 giao-thong, 11 thoi-tiet,
--   12 co-the-suc-khoe, 13 nha-cua, 14 dong-tu-thong-dung.
--   File nay dung cac topic: 1, 2, 3, 4, 7, 8, 9, 10, 13, 14.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. WORDS (id 1..75)
-- ---------------------------------------------------------------------
INSERT INTO words (id, simplified, traditional, pinyin, pinyin_numbered, meaning_vi, meaning_en, part_of_speech, hsk_level, stroke_count, frequency_rank, created_at, updated_at) VALUES
-- --- Dai tu, tu de hoi, tu chi dinh ---
(1,  '我',     '我',     'wǒ',        'wo3',            'tôi; tớ; mình',                                              'I; me',                                     'dai tu',  1, 7,  1,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(2,  '你',     '你',     'nǐ',        'ni3',            'bạn; anh; chị; em (ngôi thứ hai số ít)',                     'you (singular)',                            'dai tu',  1, 7,  2,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(3,  '他',     '他',     'tā',        'ta1',            'anh ấy; ông ấy; cậu ấy (nam)',                               'he; him',                                   'dai tu',  1, 5,  3,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(4,  '她',     '她',     'tā',        'ta1',            'cô ấy; chị ấy; bà ấy (nữ)',                                  'she; her',                                  'dai tu',  1, 6,  4,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(5,  '我们',   '我們',   'wǒmen',     'wo3 men5',       'chúng tôi; chúng ta',                                        'we; us',                                    'dai tu',  1, 7,  5,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(6,  '谁',     '誰',     'shéi',      'shei2',          'ai; người nào',                                              'who; whom',                                 'dai tu',  1, 10, 6,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(7,  '什么',   '什麼',   'shénme',    'shen2 me5',      'cái gì; gì; nào',                                            'what',                                      'dai tu',  1, 4,  7,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(8,  '哪',     '哪',     'nǎ',        'na3',            'nào (dùng để hỏi lựa chọn)',                                 'which',                                     'dai tu',  1, 9,  8,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(9,  '哪儿',   '哪兒',   'nǎr',       'na3r',           'ở đâu; chỗ nào; đâu',                                        'where',                                     'dai tu',  1, 9,  9,  '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(10, '这',     '這',     'zhè',       'zhe4',           'này; đây; cái này',                                          'this; here',                                'dai tu',  1, 7,  10, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(11, '那',     '那',     'nà',        'na4',            'kia; đó; cái kia',                                           'that; there',                               'dai tu',  1, 6,  11, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(12, '人',     '人',     'rén',       'ren2',           'người; con người',                                           'person; people',                            'danh tu', 1, 2,  12, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
-- --- So tu va luong tu ---
(13, '一',     '一',     'yī',        'yi1',            'một; số 1',                                                  'one',                                       'so tu',   1, 1,  13, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(14, '二',     '二',     'èr',        'er4',            'hai; số 2',                                                  'two',                                       'so tu',   1, 2,  14, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(15, '三',     '三',     'sān',       'san1',           'ba; số 3',                                                   'three',                                     'so tu',   1, 3,  15, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(16, '四',     '四',     'sì',        'si4',            'bốn; số 4',                                                  'four',                                      'so tu',   1, 5,  16, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(17, '五',     '五',     'wǔ',        'wu3',            'năm; số 5',                                                  'five',                                      'so tu',   1, 4,  17, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(18, '六',     '六',     'liù',       'liu4',           'sáu; số 6',                                                  'six',                                       'so tu',   1, 4,  18, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(19, '七',     '七',     'qī',        'qi1',            'bảy; số 7',                                                  'seven',                                     'so tu',   1, 2,  19, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(20, '八',     '八',     'bā',        'ba1',            'tám; số 8',                                                  'eight',                                     'so tu',   1, 2,  20, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(21, '九',     '九',     'jiǔ',       'jiu3',           'chín; số 9',                                                 'nine',                                      'so tu',   1, 2,  21, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(22, '十',     '十',     'shí',       'shi2',           'mười; số 10',                                                'ten',                                       'so tu',   1, 2,  22, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(23, '几',     '幾',     'jǐ',        'ji3',            'mấy; bao nhiêu (thường dưới 10)',                            'how many; a few',                           'so tu',   1, 2,  23, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(24, '多少',   '多少',   'duōshao',   'duo1 shao5',     'bao nhiêu',                                                  'how many; how much',                        'dai tu',  1, 6,  24, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(25, '个',     '個',     'gè',        'ge4',            'cái; chiếc; người (lượng từ dùng chung nhất)',               'general measure word',                      'luong tu',1, 3,  25, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(26, '块',     '塊',     'kuài',      'kuai4',          'đồng (đơn vị tiền, khẩu ngữ của 元); miếng; cục',            'yuan (colloquial); piece; lump',            'luong tu',1, 7,  26, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(27, '本',     '本',     'běn',       'ben3',           'quyển; cuốn (lượng từ cho sách vở)',                         'measure word for books',                    'luong tu',1, 5,  27, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(28, '些',     '些',     'xiē',       'xie1',           'một ít; một vài; những',                                     'some; a few',                               'luong tu',1, 8,  28, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(29, '岁',     '歲',     'suì',       'sui4',           'tuổi',                                                       'year of age',                               'luong tu',1, 6,  29, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
-- --- Chao hoi, phep lich su, xung ho ---
(30, '好',     '好',     'hǎo',       'hao3',           'tốt; hay; khỏe; ổn',                                         'good; well; fine',                          'tinh tu', 1, 6,  30, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(31, '你好',   '你好',   'nǐ hǎo',    'ni3 hao3',       'xin chào; chào bạn',                                         'hello; hi',                                 'than tu', 1, 7,  31, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(32, '谢谢',   '謝謝',   'xièxie',    'xie4 xie5',      'cảm ơn; cám ơn',                                             'thanks; to thank',                          'dong tu', 1, 12, 32, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(33, '不客气', '不客氣', 'bú kèqi',   'bu2 ke4 qi5',    'không có gì; đừng khách sáo',                                'you are welcome; do not mention it',        'than tu', 1, 4,  33, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(34, '再见',   '再見',   'zàijiàn',   'zai4 jian4',     'tạm biệt; hẹn gặp lại',                                      'goodbye; see you again',                    'than tu', 1, 6,  34, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(35, '对不起', '對不起', 'duìbuqǐ',   'dui4 bu5 qi3',   'xin lỗi; thành thật xin lỗi',                                'sorry; excuse me',                          'dong tu', 1, 5,  35, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(36, '没关系', '沒關係', 'méi guānxi','mei2 guan1 xi5', 'không sao; không có gì; không hề gì',                        'it does not matter; never mind',            'than tu', 1, 7,  36, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(37, '请',     '請',     'qǐng',      'qing3',          'mời; xin mời; xin hãy',                                      'please; to invite; to request',             'dong tu', 1, 10, 37, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(38, '喂',     '喂',     'wèi',       'wei4',           'a lô (khi nghe điện thoại); này (gọi ai đó); khi gọi điện thoại thường đọc thành wéi', 'hello (on the phone); hey', 'than tu', 1, 12, 38, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(39, '叫',     '叫',     'jiào',      'jiao4',          'gọi; tên là; kêu; sai bảo',                                  'to be called; to call; to shout',           'dong tu', 1, 5,  39, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(40, '名字',   '名字',   'míngzi',    'ming2 zi5',      'tên; họ tên',                                                'name',                                      'danh tu', 1, 6,  40, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(41, '认识',   '認識',   'rènshi',    'ren4 shi5',      'quen; quen biết; nhận ra; biết (ai đó, mặt chữ)',            'to know; to be acquainted with; to recognize','dong tu',1, 4,  41, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(42, '高兴',   '高興',   'gāoxìng',   'gao1 xing4',     'vui; vui vẻ; vui mừng; phấn khởi',                           'happy; glad; pleased',                      'tinh tu', 1, 10, 42, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(43, '先生',   '先生',   'xiānsheng', 'xian1 sheng5',   'ông; ngài; anh (cách gọi lịch sự với nam giới); chồng',      'Mr.; sir; husband',                         'danh tu', 1, 6,  43, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(44, '小姐',   '小姐',   'xiǎojiě',   'xiao3 jie3',     'cô; quý cô (cách gọi lịch sự với nữ giới trẻ)',              'Miss; young lady',                          'danh tu', 1, 3,  44, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(45, '朋友',   '朋友',   'péngyou',   'peng2 you5',     'bạn; bạn bè',                                                'friend',                                    'danh tu', 1, 8,  45, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(46, '同学',   '同學',   'tóngxué',   'tong2 xue2',     'bạn học; bạn cùng lớp',                                      'classmate; schoolmate',                     'danh tu', 1, 6,  46, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(47, '老师',   '老師',   'lǎoshī',    'lao3 shi1',      'giáo viên; thầy giáo; cô giáo',                              'teacher',                                   'danh tu', 1, 6,  47, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(48, '学生',   '學生',   'xuésheng',  'xue2 sheng5',    'học sinh; sinh viên',                                        'student; pupil',                            'danh tu', 1, 8,  48, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
-- --- Gia dinh ---
(49, '爸爸',   '爸爸',   'bàba',      'ba4 ba5',        'bố; ba; cha',                                                'dad; father',                               'danh tu', 1, 8,  49, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(50, '妈妈',   '媽媽',   'māma',      'ma1 ma5',        'mẹ; má',                                                     'mom; mother',                               'danh tu', 1, 6,  50, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(51, '儿子',   '兒子',   'érzi',      'er2 zi5',        'con trai',                                                   'son',                                       'danh tu', 1, 2,  51, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(52, '女儿',   '女兒',   'nǚ''ér',    'nv3 er2',        'con gái',                                                    'daughter',                                  'danh tu', 1, 3,  52, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(53, '家',     '家',     'jiā',       'jia1',           'nhà; gia đình',                                              'home; family; house',                       'danh tu', 1, 10, 53, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(54, '爱',     '愛',     'ài',        'ai4',            'yêu; yêu thương; thích',                                     'to love; to like',                          'dong tu', 1, 10, 54, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
-- --- Thoi gian ---
(55, '今天',   '今天',   'jīntiān',   'jin1 tian1',     'hôm nay',                                                    'today',                                     'danh tu', 1, 4,  55, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(56, '明天',   '明天',   'míngtiān',  'ming2 tian1',    'ngày mai',                                                   'tomorrow',                                  'danh tu', 1, 8,  56, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(57, '昨天',   '昨天',   'zuótiān',   'zuo2 tian1',     'hôm qua',                                                    'yesterday',                                 'danh tu', 1, 9,  57, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(58, '年',     '年',     'nián',      'nian2',          'năm',                                                        'year',                                      'danh tu', 1, 6,  58, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(59, '月',     '月',     'yuè',       'yue4',           'tháng; mặt trăng',                                           'month; moon',                               'danh tu', 1, 4,  59, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(60, '号',     '號',     'hào',       'hao4',           'ngày (trong tháng); số; cỡ',                                 'day of the month; number; size',            'danh tu', 1, 5,  60, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(61, '星期',   '星期',   'xīngqī',    'xing1 qi1',      'tuần; tuần lễ; thứ (trong tuần)',                            'week; day of the week',                     'danh tu', 1, 9,  61, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(62, '现在',   '現在',   'xiànzài',   'xian4 zai4',     'bây giờ; hiện nay; hiện tại',                                'now; at present',                           'danh tu', 1, 8,  62, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(63, '时候',   '時候',   'shíhou',    'shi2 hou5',      'lúc; khi; thời điểm; khoảng thời gian',                      'time; moment; while',                       'danh tu', 1, 7,  63, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(64, '点',     '點',     'diǎn',      'dian3',          'giờ (chỉ giờ đồng hồ); điểm; chấm; một chút',                'hour; point; dot; a little',                'luong tu',1, 9,  64, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(65, '分钟',   '分鐘',   'fēnzhōng',  'fen1 zhong1',    'phút',                                                       'minute',                                    'luong tu',1, 4,  65, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(66, '上午',   '上午',   'shàngwǔ',   'shang4 wu3',     'buổi sáng; trước buổi trưa',                                 'morning; before noon',                      'danh tu', 1, 3,  66, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(67, '中午',   '中午',   'zhōngwǔ',   'zhong1 wu3',     'buổi trưa; giữa trưa',                                       'noon; midday',                              'danh tu', 1, 4,  67, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(68, '下午',   '下午',   'xiàwǔ',     'xia4 wu3',       'buổi chiều',                                                 'afternoon',                                 'danh tu', 1, 3,  68, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
-- --- Dong tu co ban ---
(69, '是',     '是',     'shì',       'shi4',           'là; đúng; phải',                                             'to be; yes; correct',                       'dong tu', 1, 9,  69, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(70, '有',     '有',     'yǒu',       'you3',           'có; sở hữu; tồn tại',                                        'to have; there is; to exist',               'dong tu', 1, 6,  70, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(71, '在',     '在',     'zài',       'zai4',           'ở; ở tại; đang (làm gì đó)',                                 'to be at; in; at; in the middle of',        'dong tu', 1, 6,  71, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(72, '去',     '去',     'qù',        'qu4',            'đi; đi đến',                                                 'to go; to go to',                           'dong tu', 1, 5,  72, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(73, '来',     '來',     'lái',       'lai2',           'đến; tới; lại đây',                                          'to come; to arrive',                        'dong tu', 1, 7,  73, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(74, '回',     '回',     'huí',       'hui2',           'về; trở về; quay lại',                                       'to return; to go back',                     'dong tu', 1, 6,  74, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(75, '住',     '住',     'zhù',       'zhu4',           'ở; sống; cư trú; dừng lại',                                  'to live; to reside; to stay',               'dong tu', 1, 7,  75, '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- ---------------------------------------------------------------------
-- 2. WORD_EXAMPLES - moi tu 1 cau vi du ngan trinh do HSK 1
-- ---------------------------------------------------------------------
INSERT INTO word_examples (word_id, sentence_zh, sentence_pinyin, sentence_vi, sort_order) VALUES
(1,  '我是学生。',           'Wǒ shì xuésheng.',                'Tôi là học sinh.',                          0),
(2,  '你好吗？',             'Nǐ hǎo ma?',                      'Bạn có khỏe không?',                        0),
(3,  '他是我的朋友。',       'Tā shì wǒ de péngyou.',           'Anh ấy là bạn của tôi.',                    0),
(4,  '她是老师。',           'Tā shì lǎoshī.',                  'Cô ấy là giáo viên.',                       0),
(5,  '我们是同学。',         'Wǒmen shì tóngxué.',              'Chúng tôi là bạn học.',                     0),
(6,  '他是谁？',             'Tā shì shéi?',                    'Anh ấy là ai vậy?',                         0),
(7,  '你叫什么名字？',       'Nǐ jiào shénme míngzi?',          'Bạn tên là gì?',                            0),
(8,  '你是哪国人？',         'Nǐ shì nǎ guó rén?',              'Bạn là người nước nào?',                    0),
(9,  '你在哪儿？',           'Nǐ zài nǎr?',                     'Bạn đang ở đâu?',                           0),
(10, '这是我的书。',         'Zhè shì wǒ de shū.',              'Đây là quyển sách của tôi.',                0),
(11, '那是他的家。',         'Nà shì tā de jiā.',               'Kia là nhà của anh ấy.',                    0),
(12, '我家有四个人。',       'Wǒ jiā yǒu sì ge rén.',           'Nhà tôi có bốn người.',                     0),
(13, '我有一个儿子。',       'Wǒ yǒu yí ge érzi.',              'Tôi có một người con trai.',                0),
(14, '我有二十块钱。',       'Wǒ yǒu èrshí kuài qián.',         'Tôi có hai mươi tệ.',                       0),
(15, '现在是三点。',         'Xiànzài shì sān diǎn.',           'Bây giờ là ba giờ.',                        0),
(16, '我们有四本书。',       'Wǒmen yǒu sì běn shū.',           'Chúng tôi có bốn quyển sách.',              0),
(17, '他有五个苹果。',       'Tā yǒu wǔ ge píngguǒ.',           'Anh ấy có năm quả táo.',                    0),
(18, '今天是六号。',         'Jīntiān shì liù hào.',            'Hôm nay là ngày mùng sáu.',                 0),
(19, '我七点去学校。',       'Wǒ qī diǎn qù xuéxiào.',          'Bảy giờ tôi đi đến trường.',                0),
(20, '他今年八岁。',         'Tā jīnnián bā suì.',              'Năm nay cậu ấy tám tuổi.',                  0),
(21, '现在九点了。',         'Xiànzài jiǔ diǎn le.',            'Bây giờ chín giờ rồi.',                     0),
(22, '这里有十个人。',       'Zhèlǐ yǒu shí ge rén.',           'Ở đây có mười người.',                      0),
(23, '现在几点？',           'Xiànzài jǐ diǎn?',                'Bây giờ mấy giờ rồi?',                      0),
(24, '这个多少钱？',         'Zhège duōshao qián?',             'Cái này bao nhiêu tiền?',                   0),
(25, '我有三个朋友。',       'Wǒ yǒu sān ge péngyou.',          'Tôi có ba người bạn.',                      0),
(26, '这本书二十块。',       'Zhè běn shū èrshí kuài.',         'Quyển sách này hai mươi tệ.',               0),
(27, '我买了一本书。',       'Wǒ mǎile yì běn shū.',            'Tôi đã mua một quyển sách.',                0),
(28, '我买了一些水果。',     'Wǒ mǎile yìxiē shuǐguǒ.',         'Tôi đã mua một ít hoa quả.',                0),
(29, '我女儿五岁。',         'Wǒ nǚ''ér wǔ suì.',               'Con gái tôi năm tuổi.',                     0),
(30, '今天天气很好。',       'Jīntiān tiānqì hěn hǎo.',         'Hôm nay thời tiết rất đẹp.',                0),
(31, '你好！我是李老师。',   'Nǐ hǎo! Wǒ shì Lǐ lǎoshī.',       'Xin chào! Tôi là cô giáo Lý.',              0),
(32, '谢谢你！',             'Xièxie nǐ!',                      'Cảm ơn bạn!',                               0),
(33, '不客气，再见！',       'Bú kèqi, zàijiàn!',               'Không có gì, tạm biệt nhé!',                0),
(34, '老师，再见！',         'Lǎoshī, zàijiàn!',                'Thưa cô, em chào cô ạ! (tạm biệt)',         0),
(35, '对不起，我不能去。',   'Duìbuqǐ, wǒ bù néng qù.',         'Xin lỗi, tôi không thể đi được.',           0),
(36, '没关系，我们明天去。', 'Méi guānxi, wǒmen míngtiān qù.',  'Không sao, ngày mai chúng ta đi.',          0),
(37, '请坐，喝杯茶。',       'Qǐng zuò, hē bēi chá.',           'Mời ngồi, uống chén trà ạ.',                0),
(38, '喂，你是谁？',         'Wèi, nǐ shì shéi?',               'A lô, ai đấy ạ?',                           0),
(39, '我叫王小明。',         'Wǒ jiào Wáng Xiǎomíng.',          'Tôi tên là Vương Tiểu Minh.',               0),
(40, '他的名字是李明。',     'Tā de míngzi shì Lǐ Míng.',       'Tên của anh ấy là Lý Minh.',                0),
(41, '认识你很高兴。',       'Rènshi nǐ hěn gāoxìng.',          'Rất vui được quen biết bạn.',               0),
(42, '今天我很高兴。',       'Jīntiān wǒ hěn gāoxìng.',         'Hôm nay tôi rất vui.',                      0),
(43, '王先生是我的老师。',   'Wáng xiānsheng shì wǒ de lǎoshī.','Ông Vương là thầy giáo của tôi.',           0),
(44, '李小姐在饭馆工作。',   'Lǐ xiǎojiě zài fànguǎn gōngzuò.', 'Cô Lý làm việc ở nhà hàng.',                0),
(45, '他是我的好朋友。',     'Tā shì wǒ de hǎo péngyou.',       'Anh ấy là bạn tốt của tôi.',                0),
(46, '我的同学都是中国人。', 'Wǒ de tóngxué dōu shì Zhōngguó rén.','Các bạn học của tôi đều là người Trung Quốc.', 0),
(47, '我们的老师很好。',     'Wǒmen de lǎoshī hěn hǎo.',        'Thầy giáo của chúng tôi rất tốt.',          0),
(48, '我是学生，他是老师。', 'Wǒ shì xuésheng, tā shì lǎoshī.', 'Tôi là học sinh, còn anh ấy là giáo viên.', 0),
(49, '我爸爸是医生。',       'Wǒ bàba shì yīshēng.',            'Bố tôi là bác sĩ.',                         0),
(50, '我妈妈在家。',         'Wǒ māma zài jiā.',                'Mẹ tôi đang ở nhà.',                        0),
(51, '我的儿子六岁了。',     'Wǒ de érzi liù suì le.',          'Con trai tôi sáu tuổi rồi.',                0),
(52, '他的女儿是学生。',     'Tā de nǚ''ér shì xuésheng.',      'Con gái của anh ấy là học sinh.',           0),
(53, '我家有五个人。',       'Wǒ jiā yǒu wǔ ge rén.',           'Nhà tôi có năm người.',                     0),
(54, '我爱我的家。',         'Wǒ ài wǒ de jiā.',                'Tôi yêu gia đình của tôi.',                 0),
(55, '今天是星期一。',       'Jīntiān shì xīngqīyī.',           'Hôm nay là thứ Hai.',                       0),
(56, '明天我去北京。',       'Míngtiān wǒ qù Běijīng.',         'Ngày mai tôi đi Bắc Kinh.',                 0),
(57, '昨天我在家。',         'Zuótiān wǒ zài jiā.',             'Hôm qua tôi ở nhà.',                        0),
(58, '我学汉语一年了。',     'Wǒ xué Hànyǔ yì nián le.',        'Tôi học tiếng Trung được một năm rồi.',     0),
(59, '现在是九月。',         'Xiànzài shì jiǔ yuè.',            'Bây giờ là tháng Chín.',                    0),
(60, '今天是三月八号。',     'Jīntiān shì sān yuè bā hào.',     'Hôm nay là ngày 8 tháng 3.',                0),
(61, '今天星期几？',         'Jīntiān xīngqī jǐ?',              'Hôm nay là thứ mấy?',                       0),
(62, '现在几点了？',         'Xiànzài jǐ diǎn le?',             'Bây giờ mấy giờ rồi?',                      0),
(63, '你什么时候来？',       'Nǐ shénme shíhou lái?',           'Khi nào thì bạn đến?',                      0),
(64, '现在是八点。',         'Xiànzài shì bā diǎn.',            'Bây giờ là tám giờ.',                       0),
(65, '我看了十分钟电视。',   'Wǒ kànle shí fēnzhōng diànshì.',  'Tôi đã xem ti vi mười phút.',               0),
(66, '上午我去学校。',       'Shàngwǔ wǒ qù xuéxiào.',          'Buổi sáng tôi đi đến trường.',              0),
(67, '中午我在家吃饭。',     'Zhōngwǔ wǒ zài jiā chī fàn.',     'Buổi trưa tôi ăn cơm ở nhà.',               0),
(68, '下午我们去商店。',     'Xiàwǔ wǒmen qù shāngdiàn.',       'Buổi chiều chúng tôi đi cửa hàng.',         0),
(69, '我是中国人。',         'Wǒ shì Zhōngguó rén.',            'Tôi là người Trung Quốc.',                  0),
(70, '我有很多朋友。',       'Wǒ yǒu hěn duō péngyou.',         'Tôi có rất nhiều bạn.',                     0),
(71, '我的书在桌子上。',     'Wǒ de shū zài zhuōzi shàng.',     'Sách của tôi ở trên bàn.',                  0),
(72, '我去学校。',           'Wǒ qù xuéxiào.',                  'Tôi đi đến trường.',                        0),
(73, '他明天来我家。',       'Tā míngtiān lái wǒ jiā.',         'Ngày mai anh ấy đến nhà tôi.',              0),
(74, '我五点回家。',         'Wǒ wǔ diǎn huí jiā.',             'Năm giờ tôi về nhà.',                       0),
(75, '我住在北京。',         'Wǒ zhù zài Běijīng.',             'Tôi sống ở Bắc Kinh.',                      0);

-- ---------------------------------------------------------------------
-- 3. WORD_TOPICS - gan tu vao chu de phu hop nhat (topic_id 1..14)
-- ---------------------------------------------------------------------
INSERT INTO word_topics (word_id, topic_id) VALUES
-- dai tu / tu de hoi -> chao hoi & giao tiep (topic 1)
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (9, 10), (10, 1), (11, 1), (12, 1), (12, 2),
-- so tu va luong tu -> so dem (topic 3)
(13, 3), (14, 3), (15, 3), (16, 3), (17, 3), (18, 3), (19, 3), (20, 3), (21, 3), (22, 3),
(23, 3), (23, 4), (24, 3), (24, 9), (25, 3), (26, 3), (26, 9), (27, 3), (27, 7), (28, 3), (29, 3), (29, 2),
-- chao hoi, phep lich su, xung ho (topic 1) - truong lop (7) - nghe nghiep (8)
(30, 1), (31, 1), (32, 1), (33, 1), (34, 1), (35, 1), (36, 1), (37, 1), (38, 1),
(39, 1), (39, 14), (40, 1), (41, 1), (41, 14), (42, 1), (43, 1), (43, 2), (44, 1), (45, 1), (45, 2),
(46, 7), (46, 1), (47, 7), (47, 8), (48, 7),
-- gia dinh (topic 2) - nha cua (13) - dong tu thong dung (14)
(49, 2), (50, 2), (51, 2), (52, 2), (53, 2), (53, 13), (54, 2), (54, 14),
-- thoi gian (topic 4)
(55, 4), (56, 4), (57, 4), (58, 4), (59, 4), (60, 4), (61, 4), (62, 4), (63, 4),
(64, 4), (64, 3), (65, 4), (65, 3), (66, 4), (67, 4), (68, 4),
-- dong tu co ban -> dong tu thong dung (14), them giao thong (10) / nha cua (13)
(69, 14), (70, 14), (71, 14), (72, 14), (72, 10), (73, 14), (73, 10), (74, 14), (74, 10), (75, 14), (75, 13);
