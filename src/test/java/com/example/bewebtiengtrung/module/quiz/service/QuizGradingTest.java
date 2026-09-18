package com.example.bewebtiengtrung.module.quiz.service;

import com.example.bewebtiengtrung.module.quiz.entity.Question;
import com.example.bewebtiengtrung.module.quiz.entity.QuestionOption;
import com.example.bewebtiengtrung.module.quiz.entity.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kiểm thử logic chấm điểm bài kiểm tra.
 *
 * <p>Chỉ dựng đối tượng {@code QuizServiceImpl} với toàn bộ phụ thuộc là null:
 * phương thức {@code grade} là hàm thuần, không chạm tới repository nào.
 */
class QuizGradingTest {

    private final QuizServiceImpl service =
            new QuizServiceImpl(null, null, null, null, null, null);

    /** Dựng một câu trắc nghiệm với các phương án; phương án đúng nằm ở vị trí correctIndex. */
    private Question choiceQuestion(QuestionType type, int correctIndex, String... contents) {
        Question question = new Question();
        question.setId(1L);
        question.setType(type);
        List<QuestionOption> options = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            QuestionOption option = new QuestionOption();
            option.setId((long) (i + 1));
            option.setContent(contents[i]);
            option.setIsCorrect(i == correctIndex);
            option.setSortOrder(i);
            options.add(option);
        }
        question.setOptions(options);
        return question;
    }

    /** Dựng một câu tự luận ngắn với đáp án mong đợi. */
    private Question textQuestion(QuestionType type, String correctText) {
        Question question = new Question();
        question.setId(1L);
        question.setType(type);
        question.setCorrectText(correctText);
        return question;
    }

    @Nested
    @DisplayName("Câu trắc nghiệm")
    class TracNghiem {

        @Test
        @DisplayName("Chọn đúng phương án thì được tính đúng")
        void chon_dung_thi_dung() {
            Question q = choiceQuestion(QuestionType.SINGLE_CHOICE, 1, "不客气", "谢谢", "再见", "对不起");
            assertThat(service.grade(q, 2L, null)).isTrue();
        }

        @Test
        @DisplayName("Chọn sai phương án thì bị tính sai")
        void chon_sai_thi_sai() {
            Question q = choiceQuestion(QuestionType.SINGLE_CHOICE, 1, "不客气", "谢谢", "再见", "对不起");
            assertThat(service.grade(q, 1L, null)).isFalse();
            assertThat(service.grade(q, 3L, null)).isFalse();
        }

        @Test
        @DisplayName("Không chọn gì thì bị tính sai, không văng lỗi")
        void bo_trong_thi_sai() {
            Question q = choiceQuestion(QuestionType.SINGLE_CHOICE, 0, "是", "不是");
            assertThat(service.grade(q, null, null)).isFalse();
        }

        @Test
        @DisplayName("BẢO MẬT: gửi lên id phương án của câu hỏi KHÁC thì bị tính sai")
        void option_khong_thuoc_cau_hoi_thi_sai() {
            Question q = choiceQuestion(QuestionType.SINGLE_CHOICE, 0, "是", "不是");
            // id 999 không nằm trong tập phương án của câu này
            assertThat(service.grade(q, 999L, null)).isFalse();
        }

        @Test
        @DisplayName("Dạng LISTENING và IMAGE_CHOICE chấm giống trắc nghiệm thường")
        void listening_va_image_choice_cham_nhu_trac_nghiem() {
            Question listening = choiceQuestion(QuestionType.LISTENING, 2, "爸爸", "哥哥", "妈妈", "姐姐");
            assertThat(service.grade(listening, 3L, null)).isTrue();
            assertThat(service.grade(listening, 1L, null)).isFalse();

            Question image = choiceQuestion(QuestionType.IMAGE_CHOICE, 0, "苹果", "香蕉");
            assertThat(service.grade(image, 1L, null)).isTrue();
        }
    }

    @Nested
    @DisplayName("Câu tự luận ngắn")
    class TuLuanNgan {

        @Test
        @DisplayName("Trả lời khớp chính xác thì đúng")
        void khop_chinh_xac() {
            Question q = textQuestion(QuestionType.FILL_BLANK, "是");
            assertThat(service.grade(q, null, "是")).isTrue();
        }

        @Test
        @DisplayName("Bỏ qua khoảng trắng thừa ở hai đầu")
        void bo_qua_khoang_trang_hai_dau() {
            Question q = textQuestion(QuestionType.TRANSLATION, "我叫李南。");
            assertThat(service.grade(q, null, "  我叫李南。  ")).isTrue();
        }

        @Test
        @DisplayName("Gộp khoảng trắng ở giữa nên pinyin gõ thừa dấu cách vẫn được chấp nhận")
        void gop_khoang_trang_o_giua() {
            Question q = textQuestion(QuestionType.FILL_BLANK, "ni hao");
            assertThat(service.grade(q, null, "ni    hao")).isTrue();
        }

        @Test
        @DisplayName("Không phân biệt hoa thường")
        void khong_phan_biet_hoa_thuong() {
            Question q = textQuestion(QuestionType.TRANSLATION, "Wo shi xuesheng");
            assertThat(service.grade(q, null, "WO SHI XUESHENG")).isTrue();
        }

        @Test
        @DisplayName("Trả lời sai hoặc để trống thì bị tính sai")
        void sai_hoac_trong_thi_sai() {
            Question q = textQuestion(QuestionType.FILL_BLANK, "是");
            assertThat(service.grade(q, null, "不是")).isFalse();
            assertThat(service.grade(q, null, "")).isFalse();
            assertThat(service.grade(q, null, "   ")).isFalse();
            assertThat(service.grade(q, null, null)).isFalse();
        }

        @Test
        @DisplayName("Câu thiếu đáp án mẫu thì luôn sai, không bao giờ đúng nhờ chuỗi rỗng")
        void thieu_dap_an_mau_thi_luon_sai() {
            Question q = textQuestion(QuestionType.FILL_BLANK, null);
            assertThat(service.grade(q, null, "")).isFalse();
            assertThat(service.grade(q, null, "bất kỳ")).isFalse();
        }
    }

    @Test
    @DisplayName("Câu hỏi không có loại thì bị tính sai thay vì văng lỗi")
    void thieu_loai_cau_hoi_thi_sai() {
        Question q = new Question();
        q.setId(1L);
        q.setType(null);
        assertThat(service.grade(q, 1L, "x")).isFalse();
    }
}
