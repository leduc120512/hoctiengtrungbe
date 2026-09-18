-- =====================================================================
-- V8 : Du lieu mau cho phan KIEM TRA (quizzes / questions / question_options)
-- 12 bai kiem tra: quiz 1..6 thuoc khoa hoc HSK1 (course_id = 1),
--                  quiz 7..12 thuoc khoa hoc HSK2 (course_id = 2).
-- Moi bai kiem tra co 10 cau hoi -> tong cong 120 cau (id 1..120).
-- Ty le loai cau hoi trong moi bai: 4 SINGLE_CHOICE, 2 FILL_BLANK,
--                                   2 TRANSLATION, 2 LISTENING.
-- Cac cau SINGLE_CHOICE va LISTENING deu co dung 4 phuong an, trong do
-- chi co dung 1 phuong an is_correct = b'1' (tong cong 288 dong option).
-- Ghi chu: cot word_id de NULL vi thu tu tu vung trong bang words do file
-- seed tu vung quyet dinh; de NULL bao dam khong tao lien ket sai nghia.
-- Ghi chu: cot lesson_id cung de NULL vi cac file migration hien tai (V7)
-- moi chi seed bang courses, chua chen dong nao vao bang lessons; tro toi
-- lesson_id khong ton tai se lam vi pham fk_quizzes_lesson va hong ca lan
-- import. Lien ket bai hoc van the hien qua title/description cua tung quiz.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. QUIZZES (id 1..12)
-- ---------------------------------------------------------------------
INSERT INTO quizzes (id, slug, title, description, course_id, lesson_id, hsk_level, time_limit_seconds, pass_score, published, created_at, updated_at) VALUES
(1,  'kiem-tra-tong-hop-hsk1',              'Kiểm tra tổng hợp HSK 1',                     'Bài kiểm tra tổng hợp toàn bộ kiến thức HSK 1: chào hỏi, số đếm, thời gian, gia đình, ăn uống và mua sắm. Làm bài trong 10 phút, đạt từ 60 điểm trở lên là qua.', 1, NULL, 1, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(2,  'kiem-tra-bai-1-chao-hoi',              'Kiểm tra Bài 1: Chào hỏi và giới thiệu',      'Ôn tập các mẫu câu chào hỏi, hỏi tên và giới thiệu bản thân bằng tiếng Trung ở trình độ HSK 1.', 1, NULL, 1, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(3,  'kiem-tra-bai-2-so-dem-thoi-gian',      'Kiểm tra Bài 2: Số đếm và thời gian',         'Ôn tập số đếm từ 1 đến 100, cách hỏi giờ, hỏi ngày tháng và thứ trong tuần bằng tiếng Trung.', 1, NULL, 1, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(4,  'kiem-tra-bai-3-gia-dinh',              'Kiểm tra Bài 3: Gia đình',                    'Ôn tập từ vựng về các thành viên trong gia đình, lượng từ đếm người và mẫu câu nói về số nhân khẩu.', 1, NULL, 1, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(5,  'kiem-tra-bai-4-an-uong',               'Kiểm tra Bài 4: Ăn uống',                     'Ôn tập từ vựng món ăn, đồ uống, phân biệt động từ 吃 và 喝 cùng mẫu câu diễn đạt mong muốn.', 1, NULL, 1, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(6,  'kiem-tra-bai-5-mua-sam',               'Kiểm tra Bài 5: Mua sắm và giá cả',           'Ôn tập cách hỏi giá, đơn vị tiền tệ, phân biệt 买 và 卖 cùng các lượng từ thường gặp khi mua hàng.', 1, NULL, 1, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(7,  'kiem-tra-tong-hop-hsk2',               'Kiểm tra tổng hợp HSK 2',                     'Bài kiểm tra tổng hợp trình độ HSK 2: câu so sánh với 比, trợ từ 了 và 过, cấu trúc 因为…所以… và hành động đang tiếp diễn.', 2, NULL, 2, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(8,  'kiem-tra-bai-13-thoi-gian-lich-trinh', 'Kiểm tra Bài 13: Thời gian và lịch trình',    'Ôn tập cách nói buổi trong ngày, độ dài thời gian, cấu trúc 从…到… và thói quen sinh hoạt hằng ngày.', 2, NULL, 2, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(9,  'kiem-tra-bai-14-cong-viec-hoc-tap',    'Kiểm tra Bài 14: Công việc và học tập',       'Ôn tập từ vựng nghề nghiệp, mẫu câu 在 + nơi chốn + động từ và cách nêu ý kiến với 觉得.', 2, NULL, 2, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(10, 'kiem-tra-bai-15-suc-khoe',             'Kiểm tra Bài 15: Sức khỏe',                   'Ôn tập từ vựng về bệnh tật, khám bệnh, lời khuyên sức khỏe và mẫu câu 我 + 生病/感冒 + 了.', 2, NULL, 2, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(11, 'kiem-tra-bai-16-du-lich-giao-thong',   'Kiểm tra Bài 16: Du lịch và giao thông',      'Ôn tập từ vựng phương tiện đi lại, cách hỏi đường, giới từ 离 và cấu trúc 坐 + phương tiện.', 2, NULL, 2, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000'),
(12, 'kiem-tra-bai-17-thoi-tiet-so-thich',   'Kiểm tra Bài 17: Thời tiết và sở thích',      'Ôn tập cách hỏi và miêu tả thời tiết, câu so sánh 比 và cách diễn đạt sở thích với 喜欢 / 最喜欢.', 2, NULL, 2, 600, 60, b'1', '2026-01-01 00:00:00.000000', '2026-01-01 00:00:00.000000');

-- ---------------------------------------------------------------------
-- 2. QUIZ 1 - Kiểm tra tổng hợp HSK 1 (cau hoi 1..10)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(1,  1, 'SINGLE_CHOICE', '“谢谢!”应该怎么回答?', '“Xièxie!” yīnggāi zěnme huídá?', 'Khi người khác nói “Cảm ơn!” thì đáp lại thế nào cho đúng?', '不客气', 'Khi người khác nói 谢谢 (cảm ơn), câu đáp lại chuẩn mực là 不客气 - nghĩa là “không có gì”. 对不起 là xin lỗi, 再见 là tạm biệt, còn 请问 dùng để mở lời hỏi một cách lịch sự.', 1, 1, NULL),
(2,  1, 'SINGLE_CHOICE', '“水”的拼音是什么?', '“Shuǐ” de pīnyīn shì shénme?', 'Phiên âm đúng của chữ 水 là gì?', 'shuǐ', 'Chữ 水 (nước) đọc là shuǐ, thanh 3. shuì là chữ 睡 trong 睡觉 (ngủ), còn shuī và suǐ đều không phải cách đọc của chữ này.', 1, 2, NULL),
(3,  1, 'FILL_BLANK', '我 ___ 中国人。', 'Wǒ ___ Zhōngguó rén.', 'Điền từ còn thiếu để câu có nghĩa “Tôi là người Trung Quốc”.', '是', 'Mẫu câu phán đoán “A + 是 + danh từ” dùng động từ 是 (shì) để nối chủ ngữ với danh từ chỉ danh tính. Câu hoàn chỉnh là 我是中国人。', 1, 3, NULL),
(4,  1, 'SINGLE_CHOICE', '“今天几号?”这个问题在问什么?', '“Jīntiān jǐ hào?” zhège wèntí zài wèn shénme?', 'Câu hỏi 今天几号? hỏi về điều gì?', 'Hỏi hôm nay là ngày mấy', 'Cụm 几号 (jǐ hào) dùng để hỏi ngày trong tháng. Hỏi giờ phải dùng 几点, hỏi thứ dùng 星期几, còn hỏi thời tiết dùng 天气怎么样.', 1, 4, NULL),
(5,  1, 'LISTENING', 'Nghe và chọn từ bạn nghe được: “māma”.', NULL, 'Nghe đoạn ghi âm rồi chọn chữ Hán tương ứng với âm vừa nghe.', '妈妈', 'Âm nghe được là māma, viết là 妈妈 (mẹ). 爸爸 đọc bàba (bố), 哥哥 đọc gēge (anh trai), 姐姐 đọc jiějie (chị gái).', 1, 5, NULL),
(6,  1, 'TRANSLATION', 'Tôi tên là Lý Nam.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我叫李南。', 'Mẫu câu giới thiệu tên là “我叫 + tên”. 我 (tôi) + 叫 (tên là) + 李南 cho ra câu 我叫李南。', 1, 6, NULL),
(7,  1, 'SINGLE_CHOICE', '请选择正确的量词:三 ___ 人', 'Qǐng xuǎnzé zhèngquè de liàngcí: sān ___ rén', 'Chọn lượng từ đúng cho cụm “ba người”.', '个', 'Lượng từ thông dụng nhất để đếm người là 个: 三个人. 本 dùng cho sách, 块 dùng cho tiền hoặc miếng, 杯 dùng cho cốc nước.', 1, 7, NULL),
(8,  1, 'FILL_BLANK', '这是 ___ ?', 'Zhè shì ___?', 'Điền từ để hỏi “Đây là cái gì?”.', '什么', 'Từ để hỏi 什么 (shénme) nghĩa là “cái gì”, đặt ngay tại vị trí của thành phần cần hỏi: 这是什么?', 1, 8, NULL),
(9,  1, 'LISTENING', 'Nghe và chọn con số bạn nghe được: “sān shí”.', NULL, 'Nghe đoạn ghi âm rồi chọn con số đúng.', '三十', 'Cách đọc 三十 (sān shí) tương ứng số 30. Số 13 đọc ngược lại là 十三 (shí sān), 三 là 3 và 三百 là 300.', 1, 9, NULL),
(10, 1, 'TRANSLATION', 'Hôm nay tôi rất vui.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我今天很高兴。', 'Trạng ngữ thời gian 今天 đứng trước vị ngữ; vị ngữ tính từ 高兴 phải có phó từ mức độ 很 đi kèm, cho ra câu 我今天很高兴。', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(1, '不客气', 'bú kèqi', b'1', 0),
(1, '对不起', 'duìbuqǐ', b'0', 1),
(1, '再见', 'zàijiàn', b'0', 2),
(1, '请问', 'qǐngwèn', b'0', 3),
(2, 'shuǐ', NULL, b'1', 0),
(2, 'shuī', NULL, b'0', 1),
(2, 'shuì', NULL, b'0', 2),
(2, 'suǐ', NULL, b'0', 3),
(4, 'Hỏi hôm nay là ngày mấy', NULL, b'1', 0),
(4, 'Hỏi bây giờ là mấy giờ', NULL, b'0', 1),
(4, 'Hỏi hôm nay là thứ mấy', NULL, b'0', 2),
(4, 'Hỏi hôm nay thời tiết thế nào', NULL, b'0', 3),
(5, '妈妈', 'māma', b'1', 0),
(5, '爸爸', 'bàba', b'0', 1),
(5, '哥哥', 'gēge', b'0', 2),
(5, '姐姐', 'jiějie', b'0', 3),
(7, '个', 'gè', b'1', 0),
(7, '本', 'běn', b'0', 1),
(7, '块', 'kuài', b'0', 2),
(7, '杯', 'bēi', b'0', 3),
(9, '三十 (30)', 'sānshí', b'1', 0),
(9, '十三 (13)', 'shísān', b'0', 1),
(9, '三 (3)', 'sān', b'0', 2),
(9, '三百 (300)', 'sānbǎi', b'0', 3);

-- ---------------------------------------------------------------------
-- 3. QUIZ 2 - Bài 1: Chào hỏi và giới thiệu (cau hoi 11..20)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(11, 2, 'SINGLE_CHOICE', '“你好”应该在什么时候说?', '“Nǐ hǎo” yīnggāi zài shénme shíhou shuō?', 'Nên nói 你好 vào lúc nào?', 'Khi gặp và chào một người', '你好 (nǐ hǎo) là lời chào khi gặp mặt. Lúc chia tay phải nói 再见, khi cảm ơn nói 谢谢, khi xin lỗi nói 对不起.', 1, 1, NULL),
(12, 2, 'SINGLE_CHOICE', '“再见”的意思是什么?', '“Zàijiàn” de yìsi shì shénme?', 'Từ 再见 có nghĩa là gì?', 'Tạm biệt', 'Chữ 再 (zài) nghĩa là “lại”, 见 (jiàn) nghĩa là “gặp”; ghép lại 再见 mang nghĩa “hẹn gặp lại”, tức là tạm biệt.', 1, 2, NULL),
(13, 2, 'SINGLE_CHOICE', '请选择正确的词:___ 叫什么名字?', 'Qǐng xuǎnzé zhèngquè de cí: ___ jiào shénme míngzi?', 'Chọn đại từ đúng để hỏi tên người đang nói chuyện với mình.', '你', 'Hỏi tên người đối diện phải dùng ngôi thứ hai 你: 你叫什么名字? Dùng 我 là tự hỏi chính mình, 他 là hỏi về người thứ ba, còn 们 chỉ là hậu tố tạo số nhiều.', 1, 3, NULL),
(14, 2, 'SINGLE_CHOICE', '“老师,您好!”中的“您”表示什么?', '“Lǎoshī, nín hǎo!” zhōng de “nín” biǎoshì shénme?', 'Chữ 您 trong câu 老师,您好! thể hiện điều gì?', 'Cách xưng hô lịch sự, tôn kính với người nghe', '您 (nín) là dạng lịch sự của 你, dùng khi nói với người lớn tuổi hoặc có địa vị như thầy cô, khách hàng. Số nhiều “các bạn” phải dùng 你们.', 1, 4, NULL),
(15, 2, 'FILL_BLANK', '我 ___ 阮明。', 'Wǒ ___ Ruǎn Míng.', 'Điền từ còn thiếu để câu có nghĩa “Tôi tên là Nguyễn Minh”.', '叫', 'Mẫu câu giới thiệu tên là “我叫 + tên”. Động từ 叫 (jiào) nghĩa là “được gọi là, tên là”, nên câu đúng là 我叫阮明。', 1, 5, NULL),
(16, 2, 'FILL_BLANK', '很高兴 ___ 你。', 'Hěn gāoxìng ___ nǐ.', 'Điền từ còn thiếu để câu có nghĩa “Rất vui được làm quen với bạn”.', '认识', 'Câu xã giao chuẩn khi mới gặp là 很高兴认识你 (hěn gāoxìng rènshi nǐ). Động từ 认识 nghĩa là quen biết, làm quen.', 1, 6, NULL),
(17, 2, 'TRANSLATION', 'Bạn khỏe không?', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '你好吗?', 'Câu hỏi được tạo bằng cách thêm trợ từ nghi vấn 吗 vào cuối câu trần thuật 你好, cho ra 你好吗?', 1, 7, NULL),
(18, 2, 'TRANSLATION', 'Cảm ơn thầy giáo!', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '谢谢老师!', 'Động từ 谢谢 (xièxie) đặt trước đối tượng được cảm ơn, không cần thêm giới từ: 谢谢老师!', 1, 8, NULL),
(19, 2, 'LISTENING', 'Nghe và chọn lời chào bạn nghe được: “Nǐ hǎo!”.', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán tương ứng.', '你好!', 'Âm nghe được là Nǐ hǎo, viết bằng chữ Hán là 你好 (xin chào). Các phương án còn lại đọc là zàijiàn, xièxie và duìbuqǐ.', 1, 9, NULL),
(20, 2, 'LISTENING', 'Nghe và chọn từ chỉ nghề nghiệp bạn nghe được: “lǎoshī”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '老师', 'Âm lǎoshī viết là 老师 (giáo viên). 学生 đọc xuésheng (học sinh), 医生 đọc yīshēng (bác sĩ), 朋友 đọc péngyou (bạn bè).', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(11, 'Khi gặp và chào một người', NULL, b'1', 0),
(11, 'Khi chia tay ra về', NULL, b'0', 1),
(11, 'Khi cảm ơn ai đó', NULL, b'0', 2),
(11, 'Khi xin lỗi ai đó', NULL, b'0', 3),
(12, 'Tạm biệt', NULL, b'1', 0),
(12, 'Xin chào', NULL, b'0', 1),
(12, 'Cảm ơn', NULL, b'0', 2),
(12, 'Không có gì', NULL, b'0', 3),
(13, '你', 'nǐ', b'1', 0),
(13, '我', 'wǒ', b'0', 1),
(13, '他', 'tā', b'0', 2),
(13, '们', 'men', b'0', 3),
(14, 'Cách xưng hô lịch sự, tôn kính với người nghe', NULL, b'1', 0),
(14, 'Dạng số nhiều của “bạn”', NULL, b'0', 1),
(14, 'Đại từ ngôi thứ ba', NULL, b'0', 2),
(14, 'Cách nói tắt của 你们', NULL, b'0', 3),
(19, '你好!', 'Nǐ hǎo!', b'1', 0),
(19, '再见!', 'Zàijiàn!', b'0', 1),
(19, '谢谢!', 'Xièxie!', b'0', 2),
(19, '对不起!', 'Duìbuqǐ!', b'0', 3),
(20, '老师', 'lǎoshī', b'1', 0),
(20, '学生', 'xuésheng', b'0', 1),
(20, '医生', 'yīshēng', b'0', 2),
(20, '朋友', 'péngyou', b'0', 3);

-- ---------------------------------------------------------------------
-- 4. QUIZ 3 - Bài 2: Số đếm và thời gian (cau hoi 21..30)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(21, 3, 'SINGLE_CHOICE', '“十二”是多少?', '“Shí''èr” shì duōshao?', 'Số 十二 tương ứng với số nào?', '12', 'Chữ 十 là 10, 二 là 2; khi 十 đứng trước thì cộng thêm, nên 十二 = 10 + 2 = 12. Số 20 phải viết là 二十.', 1, 1, NULL),
(22, 3, 'SINGLE_CHOICE', '“两点半”是几点?', '“Liǎng diǎn bàn” shì jǐ diǎn?', 'Cụm 两点半 chỉ mấy giờ?', '2 giờ 30 phút', '两点 là 2 giờ, 半 (bàn) nghĩa là “rưỡi”, vậy 两点半 là 2 giờ 30. Muốn nói 2 giờ 15 thì dùng 两点一刻.', 1, 2, NULL),
(23, 3, 'SINGLE_CHOICE', '在“两”和“二”中,哪一个用在“___ 个人”?', 'Zài “liǎng” hé “èr” zhōng, nǎ yí ge yòng zài “___ ge rén”?', 'Trong hai chữ 两 và 二, chữ nào dùng trước lượng từ 个?', '两', 'Trước lượng từ phải dùng 两: 两个人 (hai người). Chữ 二 chỉ dùng khi đếm số hoặc nằm trong số nhiều chữ số như 十二, 二十.', 1, 3, NULL),
(24, 3, 'SINGLE_CHOICE', '“星期日”是星期几?', '“Xīngqīrì” shì xīngqī jǐ?', '星期日 là thứ mấy trong tuần?', 'Chủ nhật', '星期日 (xīngqīrì) hoặc 星期天 đều có nghĩa là Chủ nhật. Thứ hai là 星期一, thứ sáu là 星期五, thứ bảy là 星期六.', 1, 4, NULL),
(25, 3, 'FILL_BLANK', '现在几 ___ ?', 'Xiànzài jǐ ___?', 'Điền từ còn thiếu để hỏi “Bây giờ là mấy giờ?”.', '点', 'Hỏi giờ trong tiếng Trung dùng cụm 几点 (jǐ diǎn), trong đó 点 là đơn vị “giờ”. Câu đúng là 现在几点?', 1, 5, NULL),
(26, 3, 'FILL_BLANK', '今天星期 ___ ?', 'Jīntiān xīngqī ___?', 'Điền từ còn thiếu để hỏi “Hôm nay là thứ mấy?”.', '几', 'Hỏi thứ trong tuần dùng cụm 星期几 (xīngqī jǐ); từ để hỏi 几 thay vào vị trí của con số cần biết.', 1, 6, NULL),
(27, 3, 'TRANSLATION', 'Bây giờ là 8 giờ.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '现在八点。', 'Câu chỉ giờ trong tiếng Trung không cần động từ 是, chỉ cần đặt thẳng cụm thời gian sau chủ ngữ: 现在八点。', 1, 7, NULL),
(28, 3, 'TRANSLATION', 'Hôm nay là ngày 5 tháng 10.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '今天十月五号。', 'Trật tự thời gian tiếng Trung đi từ đơn vị lớn đến nhỏ nên tháng đứng trước ngày: 十月五号. Câu ngày tháng cũng lược bỏ 是.', 1, 8, NULL),
(29, 3, 'LISTENING', 'Nghe và chọn giờ bạn nghe được: “qī diǎn shí fēn”.', NULL, 'Nghe đoạn ghi âm rồi chọn mốc giờ đúng.', '七点十分', '七点十分 nghĩa là 7 giờ 10 phút. Mốc 10 giờ 07 phút phải đọc là 十点零七分, chú ý chữ 零 khi số phút nhỏ hơn 10.', 1, 9, NULL),
(30, 3, 'LISTENING', 'Nghe và chọn con số bạn nghe được: “jiǔ shí bā”.', NULL, 'Nghe đoạn ghi âm rồi chọn con số đúng.', '九十八', '九十八 = 90 + 8 = 98. Số 89 lại đọc là 八十九, chú ý vị trí của chữ số hàng chục.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(21, '12', 'shí''èr', b'1', 0),
(21, '20', 'èrshí', b'0', 1),
(21, '21', 'èrshíyī', b'0', 2),
(21, '2', 'èr', b'0', 3),
(22, '2 giờ 30 phút', 'liǎng diǎn bàn', b'1', 0),
(22, '2 giờ 15 phút', 'liǎng diǎn yí kè', b'0', 1),
(22, '12 giờ 30 phút', 'shí''èr diǎn bàn', b'0', 2),
(22, '2 giờ đúng', 'liǎng diǎn', b'0', 3),
(23, '两', 'liǎng', b'1', 0),
(23, '二', 'èr', b'0', 1),
(23, 'Cả hai chữ đều sai', NULL, b'0', 2),
(23, 'Dùng chữ nào cũng được', NULL, b'0', 3),
(24, 'Chủ nhật', 'xīngqīrì', b'1', 0),
(24, 'Thứ hai', 'xīngqīyī', b'0', 1),
(24, 'Thứ bảy', 'xīngqīliù', b'0', 2),
(24, 'Thứ sáu', 'xīngqīwǔ', b'0', 3),
(29, '七点十分 (7:10)', 'qī diǎn shí fēn', b'1', 0),
(29, '十点零七分 (10:07)', 'shí diǎn líng qī fēn', b'0', 1),
(29, '七点 (7:00)', 'qī diǎn', b'0', 2),
(29, '七点半 (7:30)', 'qī diǎn bàn', b'0', 3),
(30, '九十八 (98)', 'jiǔshíbā', b'1', 0),
(30, '八十九 (89)', 'bāshíjiǔ', b'0', 1),
(30, '九 (9)', 'jiǔ', b'0', 2),
(30, '九百 (900)', 'jiǔbǎi', b'0', 3);

-- ---------------------------------------------------------------------
-- 5. QUIZ 4 - Bài 3: Gia đình (cau hoi 31..40)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(31, 4, 'SINGLE_CHOICE', '“爸爸”的意思是什么?', '“Bàba” de yìsi shì shénme?', 'Từ 爸爸 có nghĩa là gì?', 'Bố', '爸爸 (bàba) là bố, cha. Mẹ là 妈妈 (māma), anh trai là 哥哥 (gēge), ông nội là 爷爷 (yéye).', 1, 1, NULL),
(32, 4, 'SINGLE_CHOICE', '“你家有几口人?”中的“口”是什么?', '“Nǐ jiā yǒu jǐ kǒu rén?” zhōng de “kǒu” shì shénme?', 'Trong câu 你家有几口人?, chữ 口 giữ vai trò gì?', 'Lượng từ đếm số người trong gia đình', 'Khi nói về số nhân khẩu trong nhà, người Trung Quốc dùng lượng từ 口 chứ không dùng 个: 我家有四口人 (nhà tôi có bốn người).', 1, 2, NULL),
(33, 4, 'SINGLE_CHOICE', '“她是我妹妹。”这句话的意思是什么?', '“Tā shì wǒ mèimei.” zhè jù huà de yìsi shì shénme?', 'Câu 她是我妹妹。 có nghĩa là gì?', 'Cô ấy là em gái tôi', '妹妹 (mèimei) là em gái. Chị gái là 姐姐, em trai là 弟弟, con gái là 女儿. Đại từ 她 chỉ người nữ.', 1, 3, NULL),
(34, 4, 'SINGLE_CHOICE', '请选择正确的量词:一 ___ 狗', 'Qǐng xuǎnzé zhèngquè de liàngcí: yì ___ gǒu', 'Chọn lượng từ đúng cho cụm “một con chó”.', '只', '只 (zhī) là lượng từ dùng cho động vật nhỏ: 一只狗, 一只猫. 本 dùng cho sách, 岁 là đơn vị tuổi chứ không phải lượng từ danh từ.', 1, 4, NULL),
(35, 4, 'FILL_BLANK', '我家 ___ 五口人。', 'Wǒ jiā ___ wǔ kǒu rén.', 'Điền từ còn thiếu để câu có nghĩa “Nhà tôi có 5 người”.', '有', 'Diễn đạt sự sở hữu hoặc tồn tại dùng động từ 有 (yǒu - có). Câu hoàn chỉnh là 我家有五口人。', 1, 5, NULL),
(36, 4, 'FILL_BLANK', '你女儿今年 ___ 大?', 'Nǐ nǚ''ér jīnnián ___ dà?', 'Điền từ còn thiếu để hỏi “Con gái bạn năm nay bao nhiêu tuổi?”.', '多', 'Cụm 多大 (duō dà) dùng để hỏi tuổi của người đã lớn. Với trẻ nhỏ có thể hỏi 几岁: 你女儿今年几岁?', 1, 6, NULL),
(37, 4, 'TRANSLATION', 'Đây là mẹ tôi.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '这是我妈妈。', '这 (zhè) nghĩa là “đây”, dùng 是 để nối với danh từ. Với quan hệ thân tộc gần gũi thường lược bỏ 的, nên nói 我妈妈 chứ không cần 我的妈妈.', 1, 7, NULL),
(38, 4, 'TRANSLATION', 'Tôi có một người em trai.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我有一个弟弟。', 'Động từ 有 diễn đạt “có”; giữa số từ 一 và danh từ 弟弟 bắt buộc phải có lượng từ 个, cho ra 我有一个弟弟。', 1, 8, NULL),
(39, 4, 'LISTENING', 'Nghe và chọn từ chỉ người thân bạn nghe được: “jiějie”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '姐姐', 'Âm jiějie viết là 姐姐 (chị gái). 妹妹 đọc mèimei (em gái), 哥哥 đọc gēge (anh trai), 弟弟 đọc dìdi (em trai).', 1, 9, NULL),
(40, 4, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Wǒ jiā yǒu sān kǒu rén.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '我家有三口人。', 'Câu nghe được là 我家有三口人 - nhà tôi có ba người. Nếu là bốn người thì phải nghe thấy 四口人 (sì kǒu rén).', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(31, 'Bố', 'bàba', b'1', 0),
(31, 'Mẹ', 'māma', b'0', 1),
(31, 'Anh trai', 'gēge', b'0', 2),
(31, 'Ông nội', 'yéye', b'0', 3),
(32, 'Lượng từ đếm số người trong gia đình', 'kǒu', b'1', 0),
(32, 'Trong câu này nó có nghĩa là “cái miệng”', NULL, b'0', 1),
(32, 'Một cách gọi khác của nhà cửa', NULL, b'0', 2),
(32, 'Một từ để hỏi', NULL, b'0', 3),
(33, 'Cô ấy là em gái tôi', NULL, b'1', 0),
(33, 'Cô ấy là chị gái tôi', NULL, b'0', 1),
(33, 'Cậu ấy là em trai tôi', NULL, b'0', 2),
(33, 'Cô ấy là con gái tôi', NULL, b'0', 3),
(34, '只', 'zhī', b'1', 0),
(34, '个', 'gè', b'0', 1),
(34, '本', 'běn', b'0', 2),
(34, '岁', 'suì', b'0', 3),
(39, '姐姐', 'jiějie', b'1', 0),
(39, '妹妹', 'mèimei', b'0', 1),
(39, '哥哥', 'gēge', b'0', 2),
(39, '弟弟', 'dìdi', b'0', 3),
(40, '我家有三口人。', 'Wǒ jiā yǒu sān kǒu rén.', b'1', 0),
(40, '我家有四口人。', 'Wǒ jiā yǒu sì kǒu rén.', b'0', 1),
(40, '我们家很大。', 'Wǒmen jiā hěn dà.', b'0', 2),
(40, '我有三个朋友。', 'Wǒ yǒu sān ge péngyou.', b'0', 3);

-- ---------------------------------------------------------------------
-- 6. QUIZ 5 - Bài 4: Ăn uống (cau hoi 41..50)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(41, 5, 'SINGLE_CHOICE', '“米饭”是什么?', '“Mǐfàn” shì shénme?', 'Từ 米饭 chỉ món gì?', 'Cơm', '米饭 (mǐfàn) là cơm nấu từ gạo. Mì sợi là 面条, nước là 水, thịt là 肉.', 1, 1, NULL),
(42, 5, 'SINGLE_CHOICE', '“我想喝水。”中的“喝”用于什么动作?', '“Wǒ xiǎng hē shuǐ.” zhōng de “hē” yòngyú shénme dòngzuò?', 'Động từ 喝 trong câu 我想喝水。 dùng cho hành động nào?', 'Uống (chất lỏng)', '喝 (hē) là “uống”, chỉ đi với đồ uống như 水, 茶, 咖啡. Hành động ăn thức ăn dùng 吃, nhìn dùng 看, mua dùng 买.', 1, 2, NULL),
(43, 5, 'SINGLE_CHOICE', '请选择正确的量词:一 ___ 茶', 'Qǐng xuǎnzé zhèngquè de liàngcí: yì ___ chá', 'Chọn lượng từ đúng cho cụm “một tách trà”.', '杯', '杯 (bēi) là lượng từ cho đồ uống đựng trong cốc, tách: 一杯茶, 一杯水. 只 dùng cho động vật, 本 dùng cho sách.', 1, 3, NULL),
(44, 5, 'SINGLE_CHOICE', '“好吃”一般用来形容什么?', '“Hǎochī” yìbān yònglái xíngróng shénme?', 'Từ 好吃 thường dùng để miêu tả cái gì?', 'Món ăn ngon', '好吃 (hǎochī) là “ngon”, chỉ dùng cho thức ăn. Đồ uống ngon phải nói 好喝 (hǎohē), còn cảnh đẹp nói 好看.', 1, 4, NULL),
(45, 5, 'FILL_BLANK', '我想 ___ 一杯茶。', 'Wǒ xiǎng ___ yì bēi chá.', 'Điền động từ còn thiếu để câu có nghĩa “Tôi muốn uống một tách trà”.', '喝', 'Với đồ uống như 茶 bắt buộc dùng động từ 喝: 我想喝一杯茶。Dùng 吃 chỉ đúng khi đi với thức ăn.', 1, 5, NULL),
(46, 5, 'FILL_BLANK', '这个菜很好 ___ 。', 'Zhège cài hěn hǎo ___.', 'Điền từ còn thiếu để câu có nghĩa “Món này rất ngon”.', '吃', 'Khen món ăn ngon dùng tính từ ghép 好吃, cho ra câu 这个菜很好吃。', 1, 6, NULL),
(47, 5, 'TRANSLATION', 'Tôi muốn ăn cơm.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我想吃米饭。', 'Động từ năng nguyện 想 (xiǎng - muốn) đứng trước động từ chính 吃, cho ra câu 我想吃米饭。', 1, 7, NULL),
(48, 5, 'TRANSLATION', 'Bạn thích uống trà không?', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '你喜欢喝茶吗?', '喜欢 + cụm động từ diễn đạt sở thích; thêm trợ từ nghi vấn 吗 ở cuối để tạo câu hỏi: 你喜欢喝茶吗?', 1, 8, NULL),
(49, 5, 'LISTENING', 'Nghe và chọn tên đồ uống bạn nghe được: “chá”.', NULL, 'Nghe đoạn ghi âm rồi chọn chữ Hán đúng.', '茶', 'Âm chá viết là 茶 (trà). Chú ý phân biệt với 菜 đọc cài (món ăn, rau), 车 đọc chē (xe) và 水 đọc shuǐ (nước).', 1, 9, NULL),
(50, 5, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Wǒ bù xiǎng chī píngguǒ.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '我不想吃苹果。', 'Trong câu nghe được có phó từ phủ định 不 nên nghĩa là “tôi không muốn ăn táo”: 我不想吃苹果。', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(41, 'Cơm', 'mǐfàn', b'1', 0),
(41, 'Mì sợi', 'miàntiáo', b'0', 1),
(41, 'Nước', 'shuǐ', b'0', 2),
(41, 'Thịt', 'ròu', b'0', 3),
(42, 'Uống (chất lỏng)', 'hē', b'1', 0),
(42, 'Ăn (thức ăn)', 'chī', b'0', 1),
(42, 'Nhìn, xem', 'kàn', b'0', 2),
(42, 'Mua', 'mǎi', b'0', 3),
(43, '杯', 'bēi', b'1', 0),
(43, '个', 'gè', b'0', 1),
(43, '本', 'běn', b'0', 2),
(43, '只', 'zhī', b'0', 3),
(44, 'Món ăn ngon', 'hǎochī', b'1', 0),
(44, 'Đồ uống ngon', 'hǎohē', b'0', 1),
(44, 'Người tốt bụng', 'hǎorén', b'0', 2),
(44, 'Cảnh đẹp, dễ nhìn', 'hǎokàn', b'0', 3),
(49, '茶', 'chá', b'1', 0),
(49, '水', 'shuǐ', b'0', 1),
(49, '菜', 'cài', b'0', 2),
(49, '车', 'chē', b'0', 3),
(50, '我不想吃苹果。', 'Wǒ bù xiǎng chī píngguǒ.', b'1', 0),
(50, '我想吃苹果。', 'Wǒ xiǎng chī píngguǒ.', b'0', 1),
(50, '我不想喝水。', 'Wǒ bù xiǎng hē shuǐ.', b'0', 2),
(50, '我很喜欢苹果。', 'Wǒ hěn xǐhuan píngguǒ.', b'0', 3);

-- ---------------------------------------------------------------------
-- 7. QUIZ 6 - Bài 5: Mua sắm và giá cả (cau hoi 51..60)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(51, 6, 'SINGLE_CHOICE', '“多少钱?”这句话在问什么?', '“Duōshao qián?” zhè jù huà zài wèn shénme?', 'Câu 多少钱? dùng để hỏi điều gì?', 'Hỏi giá bao nhiêu tiền', 'Cụm 多少钱 (duōshao qián) nghĩa là “bao nhiêu tiền”, dùng để hỏi giá. Hỏi giờ dùng 几点, hỏi nơi chốn dùng 在哪儿.', 1, 1, NULL),
(52, 6, 'SINGLE_CHOICE', '买东西的时候,“块”表示什么?', 'Mǎi dōngxi de shíhou, “kuài” biǎoshì shénme?', 'Khi mua bán, chữ 块 chỉ điều gì?', 'Đơn vị tiền tệ trong khẩu ngữ', '块 (kuài) là cách nói khẩu ngữ của đơn vị tiền 元 (yuán): 十块钱 tức là 10 tệ. Trong văn viết thường ghi 元.', 1, 2, NULL),
(53, 6, 'SINGLE_CHOICE', '请选择正确的动词:我要 ___ 一本书。', 'Qǐng xuǎnzé zhèngquè de dòngcí: wǒ yào ___ yì běn shū.', 'Chọn động từ đúng cho câu “Tôi muốn mua một quyển sách”.', '买', '买 (mǎi, thanh 3) là mua, còn 卖 (mài, thanh 4) là bán - hai chữ rất giống nhau nên phải chú ý thanh điệu. Câu cần nghĩa “mua” nên chọn 买.', 1, 3, NULL),
(54, 6, 'SINGLE_CHOICE', '“太贵了!”是什么意思?', '“Tài guì le!” shì shénme yìsi?', 'Câu 太贵了! có nghĩa là gì?', 'Đắt quá!', '贵 (guì) nghĩa là đắt; cấu trúc 太…了 nhấn mạnh mức độ “quá”. Trái nghĩa với 贵 là 便宜 (piányi - rẻ).', 1, 4, NULL),
(55, 6, 'FILL_BLANK', '这本书 ___ 少钱?', 'Zhè běn shū ___ shao qián?', 'Điền từ còn thiếu để hỏi “Quyển sách này bao nhiêu tiền?”.', '多', 'Cụm hỏi giá cố định là 多少钱, không thể tách rời. Câu hoàn chỉnh là 这本书多少钱?', 1, 5, NULL),
(56, 6, 'FILL_BLANK', '我想 ___ 一些水果。', 'Wǒ xiǎng ___ yìxiē shuǐguǒ.', 'Điền động từ còn thiếu để câu có nghĩa “Tôi muốn mua một ít hoa quả”.', '买', 'Hành động “mua” là 买 (mǎi). Câu hoàn chỉnh là 我想买一些水果。', 1, 6, NULL),
(57, 6, 'TRANSLATION', 'Cái này bao nhiêu tiền?', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '这个多少钱?', '这个 nghĩa là “cái này”, đặt làm chủ ngữ rồi thêm cụm hỏi giá 多少钱 phía sau: 这个多少钱?', 1, 7, NULL),
(58, 6, 'TRANSLATION', 'Tôi mua ba quyển sách.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我买三本书。', 'Sách dùng lượng từ 本; trật tự là số từ 三 + lượng từ 本 + danh từ 书, cho ra 我买三本书。', 1, 8, NULL),
(59, 6, 'LISTENING', 'Nghe và chọn số tiền bạn nghe được: “wǔ shí kuài”.', NULL, 'Nghe đoạn ghi âm rồi chọn số tiền đúng.', '五十块', '五十块 nghĩa là 50 tệ. Nếu nghe 十五块 (shíwǔ kuài) thì mới là 15 tệ - chú ý thứ tự của 五 và 十.', 1, 9, NULL),
(60, 6, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Zhège tài guì le.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '这个太贵了。', 'Câu nghe được là 这个太贵了 - cái này đắt quá. Từ trái nghĩa 便宜 (rẻ) nằm ở một phương án khác.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(51, 'Hỏi giá bao nhiêu tiền', 'duōshao qián', b'1', 0),
(51, 'Hỏi có bao nhiêu người', 'jǐ ge rén', b'0', 1),
(51, 'Hỏi bây giờ mấy giờ', 'jǐ diǎn', b'0', 2),
(51, 'Hỏi ở chỗ nào', 'zài nǎr', b'0', 3),
(52, 'Đơn vị tiền tệ trong khẩu ngữ', 'kuài', b'1', 0),
(52, 'Lượng từ dùng cho sách', 'běn', b'0', 1),
(52, 'Một con số cụ thể', NULL, b'0', 2),
(52, 'Tên một loại hàng hóa', NULL, b'0', 3),
(53, '买', 'mǎi', b'1', 0),
(53, '卖', 'mài', b'0', 1),
(53, '看', 'kàn', b'0', 2),
(53, '住', 'zhù', b'0', 3),
(54, 'Đắt quá!', 'tài guì le', b'1', 0),
(54, 'Rẻ quá!', 'tài piányi le', b'0', 1),
(54, 'Đẹp quá!', 'tài hǎokàn le', b'0', 2),
(54, 'Nhiều quá!', 'tài duō le', b'0', 3),
(59, '五十块 (50 tệ)', 'wǔshí kuài', b'1', 0),
(59, '十五块 (15 tệ)', 'shíwǔ kuài', b'0', 1),
(59, '五块 (5 tệ)', 'wǔ kuài', b'0', 2),
(59, '五百块 (500 tệ)', 'wǔbǎi kuài', b'0', 3),
(60, '这个太贵了。', 'Zhège tài guì le.', b'1', 0),
(60, '这个很便宜。', 'Zhège hěn piányi.', b'0', 1),
(60, '这个太大了。', 'Zhège tài dà le.', b'0', 2),
(60, '这个不好看。', 'Zhège bù hǎokàn.', b'0', 3);

-- ---------------------------------------------------------------------
-- 8. QUIZ 7 - Kiểm tra tổng hợp HSK 2 (cau hoi 61..70)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(61, 7, 'SINGLE_CHOICE', '“因为…所以…”这个结构表示什么关系?', '“Yīnwèi… suǒyǐ…” zhège jiégòu biǎoshì shénme guānxi?', 'Cấu trúc 因为…所以… biểu thị quan hệ gì?', 'Nguyên nhân - kết quả', '因为 (yīnwèi) nêu nguyên nhân, 所以 (suǒyǐ) nêu kết quả: 因为下雨,所以我没去 - vì trời mưa nên tôi không đi.', 1, 1, NULL),
(62, 7, 'SINGLE_CHOICE', '“我已经吃过饭了。”中的“过”表示什么?', '“Wǒ yǐjīng chī guo fàn le.” zhōng de “guo” biǎoshì shénme?', 'Trợ từ 过 trong câu 我已经吃过饭了。 biểu thị điều gì?', 'Việc đã từng trải qua hoặc đã hoàn tất', '过 (guo) là trợ từ động thái đặt sau động từ, chỉ kinh nghiệm hoặc việc đã trải qua. Kết hợp 已经…了 nhấn mạnh nghĩa “đã… rồi”.', 1, 2, NULL),
(63, 7, 'SINGLE_CHOICE', '比较句“他比我高”的意思是什么?', 'Bǐjiào jù “tā bǐ wǒ gāo” de yìsi shì shénme?', 'Câu so sánh 他比我高 có nghĩa là gì?', 'Anh ấy cao hơn tôi', 'Cấu trúc so sánh “A + 比 + B + tính từ” nghĩa là A hơn B ở mặt đó, nên 他比我高 nghĩa là anh ấy cao hơn tôi.', 1, 3, NULL),
(64, 7, 'SINGLE_CHOICE', '请选择正确的字:我听 ___ 懂。', 'Qǐng xuǎnzé zhèngquè de zì: wǒ tīng ___ dǒng.', 'Chọn chữ đúng để tạo bổ ngữ khả năng “nghe hiểu được”.', '得', 'Bổ ngữ khả năng dạng khẳng định dùng 得 nối giữa động từ và bổ ngữ: 听得懂 (nghe hiểu được). 的 dùng với định ngữ, 地 dùng với trạng ngữ.', 1, 4, NULL),
(65, 7, 'FILL_BLANK', '他正 ___ 看电视呢。', 'Tā zhèng ___ kàn diànshì ne.', 'Điền từ còn thiếu để câu có nghĩa “Anh ấy đang xem tivi”.', '在', 'Cấu trúc 正在… 呢 diễn đạt hành động đang tiếp diễn ngay lúc nói. Câu hoàn chỉnh là 他正在看电视呢。', 1, 5, NULL),
(66, 7, 'FILL_BLANK', '今天 ___ 昨天冷。', 'Jīntiān ___ zuótiān lěng.', 'Điền từ còn thiếu để câu có nghĩa “Hôm nay lạnh hơn hôm qua”.', '比', 'So sánh hơn dùng giới từ 比 đặt giữa hai đối tượng: A 比 B + tính từ, cho ra 今天比昨天冷。', 1, 6, NULL),
(67, 7, 'TRANSLATION', 'Tôi đã học tiếng Trung hai năm rồi.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我学汉语两年了。', 'Bổ ngữ thời lượng 两年 đặt sau động từ 学; trợ từ 了 cuối câu cho biết việc học kéo dài đến hiện tại: 我学汉语两年了。', 1, 7, NULL),
(68, 7, 'TRANSLATION', 'Vì trời mưa nên tôi không đi.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '因为下雨,所以我没去。', 'Cặp liên từ 因为…所以… nối vế nguyên nhân với vế kết quả; phủ định việc đã xảy ra trong quá khứ dùng 没 chứ không dùng 不.', 1, 8, NULL),
(69, 7, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Wǒ zuótiān qù le yīyuàn.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '我昨天去了医院。', '昨天 là hôm qua, 医院 là bệnh viện, trợ từ 了 sau động từ cho biết hành động đã hoàn thành: 我昨天去了医院。', 1, 9, NULL),
(70, 7, 'LISTENING', 'Nghe và chọn từ chỉ thời tiết bạn nghe được: “xiàyǔ”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '下雨', 'Âm xiàyǔ viết là 下雨 (trời mưa). 下雪 (xiàxuě) là tuyết rơi, 晴天 (qíngtiān) là trời nắng, 阴天 (yīntiān) là trời âm u.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(61, 'Nguyên nhân - kết quả', 'yīnwèi… suǒyǐ…', b'1', 0),
(61, 'Điều kiện - giả thiết', 'rúguǒ… jiù…', b'0', 1),
(61, 'Tương phản, nhượng bộ', 'suīrán… dànshì…', b'0', 2),
(61, 'Trình tự thời gian', 'xiān… ránhòu…', b'0', 3),
(62, 'Việc đã từng trải qua hoặc đã hoàn tất', 'guo', b'1', 0),
(62, 'Hành động đang diễn ra', 'zhèngzài', b'0', 1),
(62, 'Hành động sắp xảy ra', 'yào… le', b'0', 2),
(62, 'Câu mệnh lệnh, yêu cầu', 'ba', b'0', 3),
(63, 'Anh ấy cao hơn tôi', 'tā bǐ wǒ gāo', b'1', 0),
(63, 'Tôi cao hơn anh ấy', 'wǒ bǐ tā gāo', b'0', 1),
(63, 'Anh ấy cao bằng tôi', 'tā gēn wǒ yíyàng gāo', b'0', 2),
(63, 'Anh ấy không cao bằng tôi', 'tā méiyǒu wǒ gāo', b'0', 3),
(64, '得', 'de', b'1', 0),
(64, '的', 'de', b'0', 1),
(64, '地', 'de', b'0', 2),
(64, '了', 'le', b'0', 3),
(69, '我昨天去了医院。', 'Wǒ zuótiān qù le yīyuàn.', b'1', 0),
(69, '我明天去医院。', 'Wǒ míngtiān qù yīyuàn.', b'0', 1),
(69, '我昨天去了学校。', 'Wǒ zuótiān qù le xuéxiào.', b'0', 2),
(69, '我今天不去医院。', 'Wǒ jīntiān bú qù yīyuàn.', b'0', 3),
(70, '下雨', 'xiàyǔ', b'1', 0),
(70, '下雪', 'xiàxuě', b'0', 1),
(70, '晴天', 'qíngtiān', b'0', 2),
(70, '阴天', 'yīntiān', b'0', 3);

-- ---------------------------------------------------------------------
-- 9. QUIZ 8 - Bài 13: Thời gian và lịch trình (cau hoi 71..80)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(71, 8, 'SINGLE_CHOICE', '“上午”指一天中的什么时间?', '“Shàngwǔ” zhǐ yì tiān zhōng de shénme shíjiān?', 'Từ 上午 chỉ khoảng thời gian nào trong ngày?', 'Buổi sáng, trước 12 giờ trưa', '上午 (shàngwǔ) là buổi sáng trước giờ trưa. Buổi chiều là 下午, buổi tối là 晚上, giữa trưa là 中午.', 1, 1, NULL),
(72, 8, 'SINGLE_CHOICE', '“分钟”和“点”有什么区别?', '“Fēnzhōng” hé “diǎn” yǒu shénme qūbié?', 'Sự khác nhau giữa 分钟 và 点 là gì?', '点 chỉ thời điểm (mấy giờ), 分钟 chỉ độ dài thời gian (số phút)', '八点 là 8 giờ - một thời điểm; 二十分钟 là 20 phút - một khoảng thời gian. Muốn nói phút của một giờ cụ thể thì dùng 分: 八点十分.', 1, 2, NULL),
(73, 8, 'SINGLE_CHOICE', '请选择正确的词:我 ___ 七点起床。', 'Qǐng xuǎnzé zhèngquè de cí: wǒ ___ qī diǎn qǐchuáng.', 'Chọn từ đúng cho câu “Ngày nào tôi cũng dậy lúc 7 giờ”.', '每天', '每天 (měitiān) nghĩa là “mỗi ngày, hằng ngày”, phù hợp để diễn đạt thói quen lặp lại: 我每天七点起床。', 1, 3, NULL),
(74, 8, 'SINGLE_CHOICE', '“从…到…”表示什么?', '“Cóng… dào…” biǎoshì shénme?', 'Cấu trúc 从…到… biểu thị điều gì?', 'Từ … đến …, chỉ điểm đầu và điểm cuối', '从八点到十点 nghĩa là “từ 8 giờ đến 10 giờ”. Cấu trúc này cũng dùng cho không gian: 从家到学校 - từ nhà đến trường.', 1, 4, NULL),
(75, 8, 'FILL_BLANK', '会议 ___ 九点开始。', 'Huìyì ___ jiǔ diǎn kāishǐ.', 'Điền từ còn thiếu để câu có nghĩa “Cuộc họp bắt đầu từ lúc 9 giờ”.', '从', 'Điểm khởi đầu của thời gian được dẫn ra bằng giới từ 从 (cóng): 会议从九点开始。', 1, 5, NULL),
(76, 8, 'FILL_BLANK', '我昨天睡了八个 ___ 。', 'Wǒ zuótiān shuì le bā ge ___.', 'Điền từ còn thiếu để câu có nghĩa “Hôm qua tôi ngủ tám tiếng”.', '小时', '小时 (xiǎoshí) là đơn vị “tiếng đồng hồ”, luôn đi cùng lượng từ 个: 八个小时. Đây là bổ ngữ thời lượng đặt sau động từ.', 1, 6, NULL),
(77, 8, 'TRANSLATION', 'Ngày nào tôi cũng dậy lúc 6 rưỡi.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我每天六点半起床。', 'Trạng ngữ thời gian 每天六点半 đứng trước động từ 起床, không được đặt sau: 我每天六点半起床。', 1, 7, NULL),
(78, 8, 'TRANSLATION', 'Ngày mai tôi phải đi làm.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '明天我要上班。', 'Động từ năng nguyện 要 (yào) diễn đạt “phải, sẽ”, đứng ngay trước động từ 上班 (đi làm): 明天我要上班。', 1, 8, NULL),
(79, 8, 'LISTENING', 'Nghe và chọn thời điểm bạn nghe được: “xiàwǔ sān diǎn”.', NULL, 'Nghe đoạn ghi âm rồi chọn mốc thời gian đúng.', '下午三点', '下午 là buổi chiều, 三点 là 3 giờ, vậy đáp án là 3 giờ chiều. Buổi sáng phải là 上午 hoặc 早上.', 1, 9, NULL),
(80, 8, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Wǒ měitiān liù diǎn bàn qǐchuáng.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '我每天六点半起床。', 'Câu nghe có 六点半 (6 giờ rưỡi) và động từ 起床 (thức dậy). Nếu là 睡觉 thì nghĩa lại thành “đi ngủ”.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(71, 'Buổi sáng, trước 12 giờ trưa', 'shàngwǔ', b'1', 0),
(71, 'Buổi chiều', 'xiàwǔ', b'0', 1),
(71, 'Buổi tối', 'wǎnshang', b'0', 2),
(71, 'Nửa đêm', 'bànyè', b'0', 3),
(72, '点 chỉ thời điểm (mấy giờ), 分钟 chỉ độ dài thời gian (số phút)', NULL, b'1', 0),
(72, 'Hai từ hoàn toàn giống nhau', NULL, b'0', 1),
(72, '点 chỉ số phút, còn 分钟 chỉ số giờ', NULL, b'0', 2),
(72, 'Cả hai đều dùng để nói ngày tháng', NULL, b'0', 3),
(73, '每天', 'měitiān', b'1', 0),
(73, '昨天', 'zuótiān', b'0', 1),
(73, '后天', 'hòutiān', b'0', 2),
(73, '那天', 'nà tiān', b'0', 3),
(74, 'Từ … đến …, chỉ điểm đầu và điểm cuối', 'cóng… dào…', b'1', 0),
(74, 'So sánh hơn kém giữa hai đối tượng', 'bǐ', b'0', 1),
(74, 'Quan hệ nguyên nhân - kết quả', 'yīnwèi… suǒyǐ…', b'0', 2),
(74, 'Phủ định hành động trong quá khứ', 'méiyǒu', b'0', 3),
(79, '下午三点 (3 giờ chiều)', 'xiàwǔ sān diǎn', b'1', 0),
(79, '早上三点 (3 giờ sáng)', 'zǎoshang sān diǎn', b'0', 1),
(79, '晚上八点 (8 giờ tối)', 'wǎnshang bā diǎn', b'0', 2),
(79, '下午三点十分 (3 giờ 10 chiều)', 'xiàwǔ sān diǎn shí fēn', b'0', 3),
(80, '我每天六点半起床。', 'Wǒ měitiān liù diǎn bàn qǐchuáng.', b'1', 0),
(80, '我每天七点半起床。', 'Wǒ měitiān qī diǎn bàn qǐchuáng.', b'0', 1),
(80, '我每天六点半睡觉。', 'Wǒ měitiān liù diǎn bàn shuìjiào.', b'0', 2),
(80, '我今天六点起床。', 'Wǒ jīntiān liù diǎn qǐchuáng.', b'0', 3);

-- ---------------------------------------------------------------------
-- 10. QUIZ 9 - Bài 14: Công việc và học tập (cau hoi 81..90)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(81, 9, 'SINGLE_CHOICE', '“上班”的意思是什么?', '“Shàngbān” de yìsi shì shénme?', 'Từ 上班 có nghĩa là gì?', 'Đi làm', '上班 (shàngbān) là đi làm, vào ca. Tan làm là 下班, đi học là 上课 hoặc 上学.', 1, 1, NULL),
(82, 9, 'SINGLE_CHOICE', '“他在公司工作。”中的“在”起什么作用?', '“Tā zài gōngsī gōngzuò.” zhōng de “zài” qǐ shénme zuòyòng?', 'Chữ 在 trong câu 他在公司工作。 giữ vai trò gì?', 'Giới từ chỉ nơi chốn (ở, tại)', 'Cấu trúc 在 + địa điểm + động từ tạo thành trạng ngữ nơi chốn đặt trước động từ chính: 他在公司工作 - anh ấy làm việc ở công ty.', 1, 2, NULL),
(83, 9, 'SINGLE_CHOICE', '请选择正确的词:在医院工作的人叫 ___ 。', 'Qǐng xuǎnzé zhèngquè de cí: zài yīyuàn gōngzuò de rén jiào ___.', 'Chọn từ đúng: Người làm việc trong bệnh viện được gọi là gì?', '医生', '医生 (yīshēng) là bác sĩ, làm việc trong 医院 (bệnh viện). 老师 dạy học ở trường, 服务员 phục vụ ở nhà hàng.', 1, 3, NULL),
(84, 9, 'SINGLE_CHOICE', '“我觉得这个工作很有意思。”中的“觉得”表示什么?', '“Wǒ juéde zhège gōngzuò hěn yǒu yìsi.” zhōng de “juéde” biǎoshì shénme?', 'Từ 觉得 trong câu trên biểu thị điều gì?', 'Nêu ý kiến, cảm nhận của người nói', '觉得 (juéde) nghĩa là “cảm thấy, thấy rằng”, dùng để nêu nhận định. Lưu ý chữ 觉 trong 睡觉 lại đọc là jiào.', 1, 4, NULL),
(85, 9, 'FILL_BLANK', '他 ___ 公司工作。', 'Tā ___ gōngsī gōngzuò.', 'Điền từ còn thiếu để câu có nghĩa “Anh ấy làm việc ở công ty”.', '在', 'Giới từ 在 dẫn ra nơi chốn và phải đứng trước động từ chính: 他在公司工作。', 1, 5, NULL),
(86, 9, 'FILL_BLANK', '我下班以后 ___ 家。', 'Wǒ xiàbān yǐhòu ___ jiā.', 'Điền động từ còn thiếu để câu có nghĩa “Sau khi tan làm tôi về nhà”.', '回', '回家 (huí jiā) nghĩa là về nhà; 以后 nghĩa là “sau khi”. Câu hoàn chỉnh là 我下班以后回家。', 1, 6, NULL),
(87, 9, 'TRANSLATION', 'Anh ấy là bác sĩ.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '他是医生。', 'Câu phán đoán dùng 是 nối chủ ngữ với danh từ chỉ nghề nghiệp, không cần thêm lượng từ: 他是医生。', 1, 7, NULL),
(88, 9, 'TRANSLATION', 'Hôm nay tôi rất bận.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我今天很忙。', 'Vị ngữ tính từ 忙 cần phó từ mức độ 很 đi kèm và không dùng 是; trạng ngữ thời gian 今天 đứng trước: 我今天很忙。', 1, 8, NULL),
(89, 9, 'LISTENING', 'Nghe và chọn tên nghề nghiệp bạn nghe được: “fúwùyuán”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '服务员', 'Âm fúwùyuán viết là 服务员 (nhân viên phục vụ). 老师 đọc lǎoshī, 医生 đọc yīshēng, 学生 đọc xuésheng.', 1, 9, NULL),
(90, 9, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Tā zài xuéxiào gōngzuò.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '他在学校工作。', '学校 là trường học, 工作 là làm việc, nên câu đúng là 他在学校工作。Nếu là 学习 thì nghĩa thành “học tập ở trường”.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(81, 'Đi làm', 'shàngbān', b'1', 0),
(81, 'Tan làm', 'xiàbān', b'0', 1),
(81, 'Đi học', 'shàngxué', b'0', 2),
(81, 'Nghỉ ngơi', 'xiūxi', b'0', 3),
(82, 'Giới từ chỉ nơi chốn (ở, tại)', 'zài', b'1', 0),
(82, 'Động từ chính của câu', NULL, b'0', 1),
(82, 'Trợ từ nghi vấn cuối câu', NULL, b'0', 2),
(82, 'Liên từ nối hai mệnh đề', NULL, b'0', 3),
(83, '医生', 'yīshēng', b'1', 0),
(83, '老师', 'lǎoshī', b'0', 1),
(83, '服务员', 'fúwùyuán', b'0', 2),
(83, '学生', 'xuésheng', b'0', 3),
(84, 'Nêu ý kiến, cảm nhận của người nói', 'juéde', b'1', 0),
(84, 'Diễn tả hành động đi ngủ', 'shuìjiào', b'0', 1),
(84, 'Diễn tả sự bắt buộc', 'bìxū', b'0', 2),
(84, 'Diễn tả thời gian đã qua', 'yǐjīng', b'0', 3),
(89, '服务员', 'fúwùyuán', b'1', 0),
(89, '老师', 'lǎoshī', b'0', 1),
(89, '医生', 'yīshēng', b'0', 2),
(89, '学生', 'xuésheng', b'0', 3),
(90, '他在学校工作。', 'Tā zài xuéxiào gōngzuò.', b'1', 0),
(90, '他在公司工作。', 'Tā zài gōngsī gōngzuò.', b'0', 1),
(90, '他在学校学习。', 'Tā zài xuéxiào xuéxí.', b'0', 2),
(90, '他不在学校。', 'Tā bú zài xuéxiào.', b'0', 3);

-- ---------------------------------------------------------------------
-- 11. QUIZ 10 - Bài 15: Sức khỏe (cau hoi 91..100)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(91,  10, 'SINGLE_CHOICE', '“我生病了。”是什么意思?', '“Wǒ shēngbìng le.” shì shénme yìsi?', 'Câu 我生病了。 có nghĩa là gì?', 'Tôi bị ốm rồi', '生病 (shēngbìng) nghĩa là bị ốm, mắc bệnh; trợ từ 了 cuối câu cho biết trạng thái mới xuất hiện: 我生病了 - tôi bị ốm rồi.', 1, 1, NULL),
(92,  10, 'SINGLE_CHOICE', '身体不舒服的时候应该去哪里?', 'Shēntǐ bù shūfu de shíhou yīnggāi qù nǎlǐ?', 'Khi trong người khó chịu thì nên đến đâu?', '医院', '不舒服 (bù shūfu) nghĩa là khó chịu, không khỏe; khi đó nên đến 医院 (bệnh viện) để khám bác sĩ.', 1, 2, NULL),
(93,  10, 'SINGLE_CHOICE', '“吃药”用越南语怎么说更准确?', '“Chī yào” yòng Yuènányǔ zěnme shuō gèng zhǔnquè?', 'Cụm 吃药 dịch sang tiếng Việt thế nào cho đúng?', 'Uống thuốc', 'Tiếng Trung nói 吃药 (nghĩa đen là “ăn thuốc”) nhưng tiếng Việt phải nói “uống thuốc”. Đây là điểm khác biệt cần nhớ khi dịch.', 1, 3, NULL),
(94,  10, 'SINGLE_CHOICE', '请选择正确的词:医生说我要多 ___ 。', 'Qǐng xuǎnzé zhèngquè de cí: yīshēng shuō wǒ yào duō ___.', 'Chọn từ đúng cho câu “Bác sĩ nói tôi phải nghỉ ngơi nhiều hơn”.', '休息', '休息 (xiūxi) nghĩa là nghỉ ngơi, phù hợp với lời khuyên của bác sĩ: 医生说我要多休息。运动 là vận động, 工作 là làm việc.', 1, 4, NULL),
(95,  10, 'FILL_BLANK', '我头 ___ ,想去医院。', 'Wǒ tóu ___, xiǎng qù yīyuàn.', 'Điền từ còn thiếu để câu có nghĩa “Tôi đau đầu, muốn đi bệnh viện”.', '疼', '疼 (téng) nghĩa là đau; 头疼 là đau đầu. Câu hoàn chỉnh là 我头疼,想去医院。', 1, 5, NULL),
(96,  10, 'FILL_BLANK', '你要多 ___ 水。', 'Nǐ yào duō ___ shuǐ.', 'Điền động từ còn thiếu để câu có nghĩa “Bạn phải uống nhiều nước”.', '喝', 'Với danh từ 水 phải dùng động từ 喝 (uống); cấu trúc 多 + động từ nghĩa là “làm việc gì nhiều hơn”: 你要多喝水。', 1, 6, NULL),
(97,  10, 'TRANSLATION', 'Tôi bị cảm rồi.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我感冒了。', '感冒 (gǎnmào) là bị cảm; trợ từ 了 cuối câu diễn đạt trạng thái mới xuất hiện, cho ra 我感冒了。', 1, 7, NULL),
(98,  10, 'TRANSLATION', 'Bác sĩ nói tôi cần nghỉ ngơi.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '医生说我要休息。', 'Động từ 说 dẫn ra lời nói gián tiếp, còn 要 diễn đạt ý “cần, phải”: 医生说我要休息。', 1, 8, NULL),
(99,  10, 'LISTENING', 'Nghe và chọn từ bạn nghe được: “yīyuàn”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '医院', 'Âm yīyuàn viết là 医院 (bệnh viện). Chú ý phân biệt với 医生 (yīshēng - bác sĩ), hai từ chỉ khác nhau ở âm tiết thứ hai.', 1, 9, NULL),
(100, 10, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Wǒ jīntiān shēntǐ bù shūfu.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '我今天身体不舒服。', 'Cụm 身体不舒服 nghĩa là “trong người khó chịu”; 今天 là hôm nay, nên câu đúng là 我今天身体不舒服。', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(91, 'Tôi bị ốm rồi', 'wǒ shēngbìng le', b'1', 0),
(91, 'Tôi khỏe lại rồi', 'wǒ hǎo le', b'0', 1),
(91, 'Tôi mệt rồi', 'wǒ lèi le', b'0', 2),
(91, 'Tôi đói rồi', 'wǒ è le', b'0', 3),
(92, '医院 (bệnh viện)', 'yīyuàn', b'1', 0),
(92, '学校 (trường học)', 'xuéxiào', b'0', 1),
(92, '商店 (cửa hàng)', 'shāngdiàn', b'0', 2),
(92, '饭店 (nhà hàng)', 'fàndiàn', b'0', 3),
(93, 'Uống thuốc', 'chī yào', b'1', 0),
(93, 'Ăn thuốc', NULL, b'0', 1),
(93, 'Mua thuốc', 'mǎi yào', b'0', 2),
(93, 'Bán thuốc', 'mài yào', b'0', 3),
(94, '休息', 'xiūxi', b'1', 0),
(94, '运动', 'yùndòng', b'0', 1),
(94, '工作', 'gōngzuò', b'0', 2),
(94, '说话', 'shuōhuà', b'0', 3),
(99, '医院', 'yīyuàn', b'1', 0),
(99, '医生', 'yīshēng', b'0', 1),
(99, '宾馆', 'bīnguǎn', b'0', 2),
(99, '公司', 'gōngsī', b'0', 3),
(100, '我今天身体不舒服。', 'Wǒ jīntiān shēntǐ bù shūfu.', b'1', 0),
(100, '我今天身体很好。', 'Wǒ jīntiān shēntǐ hěn hǎo.', b'0', 1),
(100, '我昨天身体不舒服。', 'Wǒ zuótiān shēntǐ bù shūfu.', b'0', 2),
(100, '我今天很高兴。', 'Wǒ jīntiān hěn gāoxìng.', b'0', 3);

-- ---------------------------------------------------------------------
-- 12. QUIZ 11 - Bài 16: Du lịch và giao thông (cau hoi 101..110)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(101, 11, 'SINGLE_CHOICE', '“坐飞机”的意思是什么?', '“Zuò fēijī” de yìsi shì shénme?', 'Cụm 坐飞机 có nghĩa là gì?', 'Đi máy bay', 'Cấu trúc 坐 + phương tiện nghĩa là “đi bằng…”: 坐飞机 (đi máy bay), 坐火车 (đi tàu hỏa), 坐公共汽车 (đi xe buýt).', 1, 1, NULL),
(102, 11, 'SINGLE_CHOICE', '“我要去北京旅游。”中的“旅游”是什么意思?', '“Wǒ yào qù Běijīng lǚyóu.” zhōng de “lǚyóu” shì shénme yìsi?', 'Từ 旅游 trong câu trên có nghĩa là gì?', 'Du lịch', '旅游 (lǚyóu) nghĩa là đi du lịch, tham quan: 去北京旅游 - đi du lịch Bắc Kinh.', 1, 2, NULL),
(103, 11, 'SINGLE_CHOICE', '问路时,“火车站怎么走?”是在问什么?', 'Wèn lù shí, “huǒchēzhàn zěnme zǒu?” shì zài wèn shénme?', 'Câu 火车站怎么走? dùng để hỏi điều gì?', 'Hỏi đường đến nhà ga tàu hỏa', 'Cụm 怎么走 (zěnme zǒu) nghĩa là “đi thế nào”, dùng để hỏi đường: 火车站怎么走? - Đến ga tàu đi đường nào?', 1, 3, NULL),
(104, 11, 'SINGLE_CHOICE', '请选择正确的量词:一 ___ 出租车', 'Qǐng xuǎnzé zhèngquè de liàngcí: yí ___ chūzūchē', 'Chọn lượng từ đúng cho cụm “một chiếc taxi”.', '辆', '辆 (liàng) là lượng từ dành cho xe cộ: 一辆出租车, 一辆汽车. 张 dùng cho vật phẳng, 件 dùng cho quần áo và sự việc.', 1, 4, NULL),
(105, 11, 'FILL_BLANK', '我 ___ 火车去上海。', 'Wǒ ___ huǒchē qù Shànghǎi.', 'Điền động từ còn thiếu để câu có nghĩa “Tôi đi tàu hỏa đến Thượng Hải”.', '坐', 'Đi bằng phương tiện có thể ngồi được thì dùng động từ 坐: 我坐火车去上海。', 1, 5, NULL),
(106, 11, 'FILL_BLANK', '从我家 ___ 机场很远。', 'Cóng wǒ jiā ___ jīchǎng hěn yuǎn.', 'Điền từ còn thiếu để câu có nghĩa “Từ nhà tôi đến sân bay rất xa”.', '到', 'Cặp giới từ 从…到… chỉ điểm đầu và điểm cuối của quãng đường: 从我家到机场很远。', 1, 6, NULL),
(107, 11, 'TRANSLATION', 'Tôi muốn đi Bắc Kinh du lịch.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我想去北京旅游。', 'Trật tự là 想 (muốn) + 去 + địa điểm + mục đích, nên câu đúng là 我想去北京旅游。', 1, 7, NULL),
(108, 11, 'TRANSLATION', 'Nhà ga tàu hỏa cách đây có xa không?', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '火车站离这儿远吗?', 'Giới từ 离 (lí) dùng để nêu khoảng cách giữa hai điểm: A 离 B 远吗? Câu đúng là 火车站离这儿远吗?', 1, 8, NULL),
(109, 11, 'LISTENING', 'Nghe và chọn phương tiện bạn nghe được: “gōnggòng qìchē”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '公共汽车', '公共汽车 (gōnggòng qìchē) là xe buýt. 出租车 là taxi, 火车 là tàu hỏa, 飞机 là máy bay.', 1, 9, NULL),
(110, 11, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Wǒ zuò fēijī qù Běijīng.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '我坐飞机去北京。', '飞机 là máy bay, 北京 là Bắc Kinh, nên câu đúng là 我坐飞机去北京。Nếu nghe 火车 thì phương tiện lại là tàu hỏa.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(101, 'Đi máy bay', 'zuò fēijī', b'1', 0),
(101, 'Lái ô tô', 'kāi chē', b'0', 1),
(101, 'Đi tàu hỏa', 'zuò huǒchē', b'0', 2),
(101, 'Đi bộ', 'zǒu lù', b'0', 3),
(102, 'Du lịch', 'lǚyóu', b'1', 0),
(102, 'Làm việc', 'gōngzuò', b'0', 1),
(102, 'Học tập', 'xuéxí', b'0', 2),
(102, 'Mua sắm', 'mǎi dōngxi', b'0', 3),
(103, 'Hỏi đường đến nhà ga tàu hỏa', 'zěnme zǒu', b'1', 0),
(103, 'Hỏi giá vé tàu là bao nhiêu', 'duōshao qián', b'0', 1),
(103, 'Hỏi mấy giờ tàu chạy', 'jǐ diǎn', b'0', 2),
(103, 'Hỏi tàu này đi đến đâu', 'qù nǎr', b'0', 3),
(104, '辆', 'liàng', b'1', 0),
(104, '张', 'zhāng', b'0', 1),
(104, '件', 'jiàn', b'0', 2),
(104, '只', 'zhī', b'0', 3),
(109, '公共汽车', 'gōnggòng qìchē', b'1', 0),
(109, '出租车', 'chūzūchē', b'0', 1),
(109, '火车', 'huǒchē', b'0', 2),
(109, '飞机', 'fēijī', b'0', 3),
(110, '我坐飞机去北京。', 'Wǒ zuò fēijī qù Běijīng.', b'1', 0),
(110, '我坐火车去北京。', 'Wǒ zuò huǒchē qù Běijīng.', b'0', 1),
(110, '我坐飞机去上海。', 'Wǒ zuò fēijī qù Shànghǎi.', b'0', 2),
(110, '我想去北京。', 'Wǒ xiǎng qù Běijīng.', b'0', 3);

-- ---------------------------------------------------------------------
-- 13. QUIZ 12 - Bài 17: Thời tiết và sở thích (cau hoi 111..120)
-- ---------------------------------------------------------------------
INSERT INTO questions (id, quiz_id, type, prompt, prompt_pinyin, prompt_vi, correct_text, explanation, points, sort_order, word_id) VALUES
(111, 12, 'SINGLE_CHOICE', '“今天天气怎么样?”这句话在问什么?', '“Jīntiān tiānqì zěnmeyàng?” zhè jù huà zài wèn shénme?', 'Câu 今天天气怎么样? dùng để hỏi điều gì?', 'Hỏi thời tiết hôm nay thế nào', '天气 (tiānqì) là thời tiết, 怎么样 dùng để hỏi “thế nào”, nên cả câu hỏi về tình hình thời tiết hôm nay.', 1, 1, NULL),
(112, 12, 'SINGLE_CHOICE', '“外面在下雨。”是什么意思?', '“Wàimiàn zài xiàyǔ.” shì shénme yìsi?', 'Câu 外面在下雨。 có nghĩa là gì?', 'Bên ngoài đang mưa', 'Phó từ 在 đặt trước động từ chỉ hành động đang diễn ra, 下雨 nghĩa là mưa, nên câu này nghĩa là “bên ngoài đang mưa”.', 1, 2, NULL),
(113, 12, 'SINGLE_CHOICE', '请选择正确的词:今天很 ___ ,我要多穿一点。', 'Qǐng xuǎnzé zhèngquè de cí: jīntiān hěn ___, wǒ yào duō chuān yìdiǎn.', 'Chọn từ đúng cho câu “Hôm nay rất ___, tôi phải mặc thêm áo”.', '冷', 'Phải mặc thêm quần áo là vì trời lạnh, nên chọn 冷 (lěng). 热 là nóng, 快 là nhanh, 累 là mệt - đều không hợp ngữ cảnh.', 1, 3, NULL),
(114, 12, 'SINGLE_CHOICE', '“我最喜欢踢足球。”中的“最”表示什么?', '“Wǒ zuì xǐhuan tī zúqiú.” zhōng de “zuì” biǎoshì shénme?', 'Chữ 最 trong câu 我最喜欢踢足球。 biểu thị điều gì?', 'Mức độ cao nhất, nghĩa là “nhất”', '最 (zuì) là phó từ chỉ mức độ cao nhất: 最喜欢 nghĩa là “thích nhất”. Cụm 踢足球 nghĩa là chơi bóng đá.', 1, 4, NULL),
(115, 12, 'FILL_BLANK', '今天比昨天 ___ 。', 'Jīntiān bǐ zuótiān ___.', 'Điền từ còn thiếu để câu có nghĩa “Hôm nay nóng hơn hôm qua”.', '热', 'Câu so sánh “A 比 B + tính từ” đặt tính từ ở cuối: 今天比昨天热 - hôm nay nóng hơn hôm qua.', 1, 5, NULL),
(116, 12, 'FILL_BLANK', '我喜欢 ___ 音乐。', 'Wǒ xǐhuan ___ yīnyuè.', 'Điền động từ còn thiếu để câu có nghĩa “Tôi thích nghe nhạc”.', '听', 'Nghe nhạc là cụm cố định 听音乐 (tīng yīnyuè), trong đó 听 là động từ “nghe”.', 1, 6, NULL),
(117, 12, 'TRANSLATION', 'Hôm nay trời rất lạnh.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '今天很冷。', 'Câu vị ngữ tính từ không dùng 是; tính từ 冷 đi kèm phó từ mức độ 很, cho ra câu 今天很冷。', 1, 7, NULL),
(118, 12, 'TRANSLATION', 'Tôi thích chơi bóng đá.', NULL, 'Dịch câu tiếng Việt sau sang tiếng Trung.', '我喜欢踢足球。', '喜欢 + cụm động từ diễn đạt sở thích; “chơi bóng đá” trong tiếng Trung dùng động từ 踢 (đá): 我喜欢踢足球。', 1, 8, NULL),
(119, 12, 'LISTENING', 'Nghe và chọn từ chỉ thời tiết bạn nghe được: “xiàxuě”.', NULL, 'Nghe đoạn ghi âm rồi chọn từ chữ Hán đúng.', '下雪', 'Âm xiàxuě viết là 下雪 (tuyết rơi). Còn 下雨 (xiàyǔ) mới là trời mưa - hai từ chỉ khác nhau ở âm tiết thứ hai.', 1, 9, NULL),
(120, 12, 'LISTENING', 'Nghe và chọn câu bạn nghe được: “Jīntiān tiānqì hěn hǎo.”', NULL, 'Nghe đoạn ghi âm rồi chọn câu chữ Hán đúng.', '今天天气很好。', 'Câu nghe được là 今天天气很好 - hôm nay thời tiết rất đẹp. Nếu có phó từ 不 thì nghĩa sẽ ngược lại.', 1, 10, NULL);

INSERT INTO question_options (question_id, content, pinyin, is_correct, sort_order) VALUES
(111, 'Hỏi thời tiết hôm nay thế nào', 'tiānqì zěnmeyàng', b'1', 0),
(111, 'Hỏi hôm nay là ngày mấy', 'jīntiān jǐ hào', b'0', 1),
(111, 'Hỏi hôm nay có bận không', 'jīntiān máng ma', b'0', 2),
(111, 'Hỏi hôm nay đi đâu', 'jīntiān qù nǎr', b'0', 3),
(112, 'Bên ngoài đang mưa', 'wàimiàn zài xiàyǔ', b'1', 0),
(112, 'Bên ngoài trời nắng', 'wàimiàn shì qíngtiān', b'0', 1),
(112, 'Bên ngoài đang có tuyết', 'wàimiàn zài xiàxuě', b'0', 2),
(112, 'Bên ngoài rất lạnh', 'wàimiàn hěn lěng', b'0', 3),
(113, '冷', 'lěng', b'1', 0),
(113, '热', 'rè', b'0', 1),
(113, '快', 'kuài', b'0', 2),
(113, '累', 'lèi', b'0', 3),
(114, 'Mức độ cao nhất, nghĩa là “nhất”', 'zuì', b'1', 0),
(114, 'Mức độ nhẹ, nghĩa là “hơi”', 'yǒudiǎnr', b'0', 1),
(114, 'Ý phủ định', 'bù', b'0', 2),
(114, 'Thời gian trong quá khứ', 'yǐqián', b'0', 3),
(119, '下雪', 'xiàxuě', b'1', 0),
(119, '下雨', 'xiàyǔ', b'0', 1),
(119, '刮风', 'guāfēng', b'0', 2),
(119, '晴天', 'qíngtiān', b'0', 3),
(120, '今天天气很好。', 'Jīntiān tiānqì hěn hǎo.', b'1', 0),
(120, '今天天气不好。', 'Jīntiān tiānqì bù hǎo.', b'0', 1),
(120, '昨天天气很好。', 'Zuótiān tiānqì hěn hǎo.', b'0', 2),
(120, '今天天气很热。', 'Jīntiān tiānqì hěn rè.', b'0', 3);
