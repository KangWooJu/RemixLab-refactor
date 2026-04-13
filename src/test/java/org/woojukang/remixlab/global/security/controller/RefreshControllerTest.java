package org.woojukang.remixlab.global.security.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.woojukang.remixlab.global.security.dto.response.ReissueResponse;
import org.woojukang.remixlab.global.security.service.RefreshService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RefreshController.class)
@AutoConfigureMockMvc(addFilters = false)
class RefreshControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RefreshService refreshService;

    // 성공 테스트
    @Test
    void reissue_shouldReturnNewTokens_whenRefreshExists() throws Exception {

        // given
        ReissueResponse reissueResponse =
                new ReissueResponse(
                        "REFRESH_EXISTS",
                        "Refresh EXISTS",
                        "newAccessToken",
                        "newRefreshToken"
                );

        Cookie refreshCookie = new Cookie("refresh", "newRefreshToken");

        when(refreshService.refreshCookies(any()))
                .thenReturn(reissueResponse);

        when(refreshService.createCookie(any(), any()))
                .thenReturn(refreshCookie);

        when(refreshService.reissueRefresh(any()))
                .thenReturn("newRefreshToken");

        // when & then
        mockMvc.perform(post("/api/v1/user/refresh/reissue"))
                .andExpect(status().isOk())
                .andExpect(header().string("access", "newAccessToken"))
                .andExpect(cookie().exists("refresh"));
    }

    // Refresh Cookie 없음
    @Test
    void reissue_shouldReturnBadRequest_whenRefreshCookieIsNull() throws Exception {

        // given
        ReissueResponse serviceResponse =
                new ReissueResponse(
                        "REFRESH_NULL",
                        "Refresh NULL",
                        null,
                        null
                );

        when(refreshService.refreshCookies(any()))
                .thenReturn(serviceResponse);

        // when & then
        mockMvc.perform(post("/api/v1/user/refresh/reissue"))
                .andExpect(status().isBadRequest());
    }

    // Refresh 만료
    @Test
    void reissue_shouldReturnBadRequest_whenRefreshTokenExpired() throws Exception {

        // given
        ReissueResponse serviceResponse =
                new ReissueResponse(
                        "REFRESH_EXPIRED",
                        "Refresh EXPIRED",
                        null,
                        null
                );

        when(refreshService.refreshCookies(any()))
                .thenReturn(serviceResponse);

        // when & then
        mockMvc.perform(post("/api/v1/user/refresh/reissue"))
                .andExpect(status().isBadRequest());
    }
}