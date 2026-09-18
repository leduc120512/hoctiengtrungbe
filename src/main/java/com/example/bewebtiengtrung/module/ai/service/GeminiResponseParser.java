package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.ai.dto.GeneratedSentence;
import com.example.bewebtiengtrung.module.ai.dto.SentenceBatch;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Đọc câu trả lời của Gemini {@code generateContent} thành {@link SentenceBatch}.
 *
 * <p>Tách riêng thành lớp thuần (không gọi mạng) để kiểm thử được mọi nhánh: JSON hợp lệ,
 * không có {@code candidates}, bị chặn bởi {@code promptFeedback.blockReason}, text không phải JSON.
 * Dùng Jackson 3 ({@code tools.jackson.databind}) — là ObjectMapper mà Spring Boot 4 cung cấp.
 */
public final class GeminiResponseParser {

    private GeminiResponseParser() {
    }

    /**
     * @param body   JSON thô do Gemini trả về
     * @param mapper ObjectMapper Jackson 3
     * @return các câu đã sinh (có thể rỗng nếu model trả mảng rỗng)
     * @throws ApiException 502 khi câu trả lời không dùng được (bị chặn, thiếu text, JSON hỏng)
     */
    public static SentenceBatch parse(String body, ObjectMapper mapper) {
        if (body == null || body.isBlank()) {
            throw upstream("AI trả về nội dung rỗng");
        }
        JsonNode root;
        try {
            root = mapper.readTree(body);
        } catch (RuntimeException e) {
            throw upstream("AI trả về dữ liệu không phải JSON");
        }

        JsonNode block = root.path("promptFeedback").path("blockReason");
        if (!block.isMissingNode() && !block.isNull()) {
            throw upstream("AI từ chối yêu cầu (" + block.asText() + ")");
        }

        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            throw upstream("AI không trả về câu nào");
        }
        JsonNode first = candidates.get(0);
        String finish = first.path("finishReason").asText("");
        if ("SAFETY".equals(finish) || "RECITATION".equals(finish) || "PROHIBITED_CONTENT".equals(finish)) {
            throw upstream("AI từ chối yêu cầu (" + finish + ")");
        }

        StringBuilder text = new StringBuilder();
        for (JsonNode part : first.path("content").path("parts")) {
            JsonNode t = part.path("text");
            if (t.isTextual()) {
                text.append(t.asText());
            }
        }
        if (text.isEmpty()) {
            throw upstream("AI không trả về nội dung văn bản");
        }

        JsonNode payload;
        try {
            payload = mapper.readTree(stripFences(text.toString()));
        } catch (RuntimeException e) {
            throw upstream("AI trả về câu không đúng định dạng JSON");
        }
        JsonNode arr = payload.path("sentences");
        if (!arr.isArray()) {
            throw upstream("AI trả về JSON thiếu trường sentences");
        }
        List<GeneratedSentence> out = new ArrayList<>(arr.size());
        for (JsonNode n : arr) {
            String hanzi = n.path("hanzi").asText("").trim();
            if (hanzi.isEmpty()) {
                continue;
            }
            int level = n.path("level").isInt() ? n.path("level").asInt() : 1;
            if (level < 1 || level > 3) {
                level = 1;
            }
            out.add(new GeneratedSentence(hanzi, n.path("pinyin").asText("").trim(),
                    n.path("vi").asText("").trim(), level));
        }
        return new SentenceBatch(out);
    }

    /** Một số model bọc JSON trong ```json ... ``` dù đã yêu cầu JSON thuần. */
    static String stripFences(String s) {
        String t = s.trim();
        if (t.startsWith("```")) {
            int firstNewline = t.indexOf('\n');
            t = firstNewline >= 0 ? t.substring(firstNewline + 1) : t.substring(3);
            if (t.endsWith("```")) {
                t = t.substring(0, t.length() - 3);
            }
        }
        return t.trim();
    }

    private static ApiException upstream(String message) {
        return new ApiException(HttpStatus.BAD_GATEWAY, "AI_UPSTREAM", message);
    }
}
