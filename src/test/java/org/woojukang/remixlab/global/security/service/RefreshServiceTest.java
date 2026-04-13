package org.woojukang.remixlab.global.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;
import org.woojukang.remixlab.global.security.repository.RefreshRepository;
import org.woojukang.remixlab.global.security.util.CookieUtil;
import org.woojukang.remixlab.global.security.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshRepository refreshRepository;

    @Mock
    private CookieUtil cookieUtil;

    @InjectMocks
    private RefreshService refreshService;

    /*
    refreshCookies() 메소드 테스트
     */

    // refreshCookies 메소드 - 예외 : null
    @Test
    void refreshCookies_refreshNull() {

        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(cookieUtil.findCookie(request)).thenReturn(null);

        // when
        var result = refreshService.refreshCookies(request);

        // then
        assertEquals("REFRESH_NULL", result.status());
    }

    // refreshCookies 메소드 - 예외 : 토큰 파기

    @Test
    void refreshCookies_refreshExpired() {

        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(cookieUtil.findCookie(request)).thenReturn("refresh");

        // when
        doThrow(io.jsonwebtoken.ExpiredJwtException.class)
                .when(jwtUtil).isExpired("refresh");

        var result = refreshService.refreshCookies(request);

        // then
        assertEquals("REFRESH_EXPIRED", result.status());
    }

    // refreshCookies 메소드 - 성공

    @Test
    void refreshCookies_success() {

        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(cookieUtil.findCookie(request))
                .thenReturn("refresh");

        when(jwtUtil.getUsername("refresh"))
                .thenReturn("user");

        when(jwtUtil.getRole("refresh"))
                .thenReturn("ROLE_USER");

        when(jwtUtil.createJwt(any(), any(), any(), anyLong()))
                .thenReturn("newAccess", "newRefresh");

        when(refreshRepository.findByKey("refresh"))
                .thenReturn("user");

        // when
        var result = refreshService.refreshCookies(request);

        // then
        assertEquals("REFRESH_EXISTS", result.status());
    }



    @Test
    void reissueRefresh() {

        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(cookieUtil.findCookie(request))
                .thenReturn("refresh");

        when(jwtUtil.getUsername("refresh"))
                .thenReturn("user");

        when(jwtUtil.getRole("refresh"))
                .thenReturn("ROLE_USER");

        when(jwtUtil.createJwt(any(), any(), any(), anyLong()))
                .thenReturn("newRefresh");

        when(refreshRepository.findByKey("refresh"))
                .thenReturn("user");

        // when
        String result = refreshService.reissueRefresh(request);

        // then
        assertEquals("newRefresh", result);

        verify(refreshRepository)
                .delete("refresh");

        verify(refreshRepository)
                .delete("user");

        verify(refreshRepository)
                .save(eq("user"),eq("newRefresh"), anyLong());

        verify(refreshRepository)
                .save(eq("newRefresh"), eq("user"), anyLong());
    }

    @Test
    void createCookie() {

        // given
        Cookie cookie = new Cookie("refresh", "value");

        when(cookieUtil.createCookie("refresh", "value"))
                .thenReturn(cookie);

        // when
        Cookie result = refreshService.createCookie("refresh", "value");

        // then
        assertEquals("refresh", result.getName());
        assertEquals("value", result.getValue());
    }

    @Test
    void addRefresh() {

        // given
        String username = "user";
        String refresh = "refreshToken";

        // when
        refreshService.addRefresh(username, refresh, 1000L);

        // then
        verify(refreshRepository)
                .save(username, refresh, 1000L);

        verify(refreshRepository)
                .save(refresh, username, 1000L);
    }

    @Test
    void deleteRefresh() {

        // given
        String refresh = "refreshToken";
        String username = "user";

        when(refreshRepository.findByKey(refresh)).thenReturn(username);

        // when
        refreshService.deleteRefresh(refresh);

        // then
        verify(refreshRepository).delete(refresh);
        verify(refreshRepository).delete(username);

    }

    @Test
    void getValue() {

        // given
        String key = "refreshToken";
        String username = "user";

        when(refreshRepository.findByKey(key)).thenReturn(username);

        // when
        Object result = refreshService.getValue(key);

        // then
        assertEquals(username, result);
        verify(refreshRepository).findByKey(key);

    }

    // validateAlreadyLogin 메소드 - 예외
    @Test
    void validateAlreadyLogin_shouldThrowException_whenUserAlreadyLogin() {

        // given
        String username = "user";

        when(refreshRepository.exists(username)).thenReturn(false);

        // when & then
        assertThrows(BaseException.class, () -> {
            refreshService.validateAlreadyLogin(username);
        });

        verify(refreshRepository).exists(username);

    }

    // validateAlreadyLogin 메소드 - 성공
    @Test
    void validateAlreadyLogin_shouldPass_whenUserNotLoggedIn() {
        // given
        String username = "user";

        when(refreshRepository.exists(username)).thenReturn(true);

        // when
        refreshService.validateAlreadyLogin(username);

        // then
        verify(refreshRepository).exists(username);
    }


}