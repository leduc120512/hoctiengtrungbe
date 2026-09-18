package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.ai.dto.SentenceBatch;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Kiểm thử bóc tách câu trả lời Gemini — mọi nhánh lỗi phải thành ApiException 502 có thông điệp tiếng Việt. */
class GeminiResponseParserTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();

    /** Bọc một chuỗi JSON câu vào cấu trúc generateContent của Gemini. */
    private static String envelope(String innerJson) {
        String escaped = innerJson.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"" + escaped + "\"}]},\"finishReason\":\"STOP\"}]}";
    }

    @Test
    void doc_dung_danh_sach_cau() {
        String inner = "{\"sentences\":[{\"hanzi\":\"\\u6211\\u56de\\u5bb6\\u3002\",\"pinyin\":\"W\\u01d2 hu\\u00ed ji\\u0101.\",\"vi\":\"T\\u00f4i v\\u1ec1 nh\\u00e0.\",\"level\":1},"
                + "{\"hanzi\":\"\\u4ed6\\u6765\\u4e86\\u3002\",\"pinyin\":\"T\\u0101 l\\u00e1i le.\",\"vi\":\"Anh \\u1ea5y \\u0111\\u1ebfn r\\u1ed3i.\",\"level\":9}]}";
        SentenceBatch batch = GeminiResponseParser.parse(envelope(inner), mapper);
        assertThat(batch.sentences()).hasSize(2);
        assertThat(batch.sentences().get(0).level()).isEqualTo(1);
        // Cấp ngoài khoảng 1–3 được đưa về 1 thay vì làm hỏng cả lô.
        assertThat(batch.sentences().get(1).level()).isEqualTo(1);
    }

    @Test
    void bo_hang_rao_markdown_json() {
        String inner = "```json\n{\"sentences\":[{\"hanzi\":\"\\u4f60\\u597d\\u3002\",\"pinyin\":\"N\\u01d0 h\\u01ceo.\",\"vi\":\"Xin ch\\u00e0o.\",\"level\":1}]}\n```";
        assertThat(GeminiResponseParser.parse(envelope(inner), mapper).sentences()).hasSize(1);
    }

    @Test
    void bi_chan_boi_promptFeedback() {
        String body = "{\"promptFeedback\":{\"blockReason\":\"SAFETY\"}}";
        assertThatThrownBy(() -> GeminiResponseParser.parse(body, mapper))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("từ chối");
    }

    @Test
    void khong_co_candidates() {
        assertThatThrownBy(() -> GeminiResponseParser.parse("{\"candidates\":[]}", mapper))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("không trả về câu nào");
    }

    @Test
    void text_khong_phai_json() {
        assertThatThrownBy(() -> GeminiResponseParser.parse(envelope("xin loi toi khong the"), mapper))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("định dạng JSON");
    }

    @Test
    void body_rong_hoac_hong() {
        assertThatThrownBy(() -> GeminiResponseParser.parse("", mapper)).isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> GeminiResponseParser.parse("{not json", mapper)).isInstanceOf(ApiException.class);
    }

    @Test
    void cau_thieu_hanzi_bi_bo_qua() {
        String inner = "{\"sentences\":[{\"hanzi\":\"\",\"pinyin\":\"x\",\"vi\":\"y\",\"level\":1},"
                + "{\"hanzi\":\"\\u597d\\u3002\",\"pinyin\":\"H\\u01ceo.\",\"vi\":\"T\\u1ed1t.\",\"level\":2}]}";
        assertThat(GeminiResponseParser.parse(envelope(inner), mapper).sentences()).hasSize(1);
    }
}
