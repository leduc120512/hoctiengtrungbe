package com.example.bewebtiengtrung.module.ai.service;

import com.example.bewebtiengtrung.common.exception.ApiException;
import com.example.bewebtiengtrung.module.ai.config.AiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Bảng chọn provider: cấu hình × key có sẵn. */
class AiSentenceGeneratorRouterTest {

    private static AiSentenceGeneratorRouter router(String provider, boolean claudeKey, boolean geminiKey) {
        AiProperties props = new AiProperties();
        props.setProvider(provider);
        ClaudeSentenceGenerator claude = mock(ClaudeSentenceGenerator.class);
        GeminiSentenceGenerator gemini = mock(GeminiSentenceGenerator.class);
        when(claude.isEnabled()).thenReturn(claudeKey);
        when(claude.model()).thenReturn("claude-opus-5");
        when(gemini.isEnabled()).thenReturn(geminiKey);
        when(gemini.model()).thenReturn("gemini-2.5-flash");
        return new AiSentenceGeneratorRouter(props, claude, gemini);
    }

    @Test
    void auto_uu_tien_claude_khi_co_ca_hai_key() {
        AiSentenceGeneratorRouter r = router("auto", true, true);
        assertThat(r.activeProvider()).isEqualTo(AiSentenceGeneratorRouter.Provider.CLAUDE);
        assertThat(r.model()).isEqualTo("claude-opus-5");
        assertThat(r.isEnabled()).isTrue();
    }

    @Test
    void auto_roi_ve_gemini_khi_chi_co_gemini_key() {
        AiSentenceGeneratorRouter r = router("auto", false, true);
        assertThat(r.activeProvider()).isEqualTo(AiSentenceGeneratorRouter.Provider.GEMINI);
        assertThat(r.model()).isEqualTo("gemini-2.5-flash");
    }

    @Test
    void auto_tat_khi_khong_co_key_nao() {
        AiSentenceGeneratorRouter r = router("auto", false, false);
        assertThat(r.isEnabled()).isFalse();
        assertThat(r.disabledReason()).contains("GEMINI_API_KEY").contains("ANTHROPIC_API_KEY");
        assertThatThrownBy(() -> r.generate(List.of(), List.of(), 5, null, null))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }

    @Test
    void ep_gemini_bo_qua_claude_du_co_key() {
        assertThat(router("gemini", true, true).activeProvider()).isEqualTo(AiSentenceGeneratorRouter.Provider.GEMINI);
    }

    @Test
    void ep_claude_nhung_thieu_key_thi_tat() {
        assertThat(router("claude", false, true).isEnabled()).isFalse();
    }

    @Test
    void off_tat_han() {
        assertThat(router("off", true, true).isEnabled()).isFalse();
    }
}
