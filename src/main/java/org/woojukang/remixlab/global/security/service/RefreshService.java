package org.woojukang.remixlab.global.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.global.security.dto.response.ReissueResponse;
import org.woojukang.remixlab.global.security.entity.Refresh;
import org.woojukang.remixlab.global.security.repository.RefreshRepository;
import org.woojukang.remixlab.global.security.util.JwtUtil;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public String findCookie(HttpServletRequest request) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        }

        log.info("refresh cookie:{}",refresh);



        return refresh;

    }

    @Transactional
    public ReissueResponse refreshCookies(HttpServletRequest request) {

        String refresh = findCookie(request);
        if (refresh == null) {
            return new ReissueResponse("REFRESH_NULL", "Refresh NULL " +
                    "[ Time : " + LocalDateTime.now() +
                    " ]", null, null);
        }

        try {
            jwtUtil.isExpired(refresh);

        } catch (ExpiredJwtException e) {
            return new ReissueResponse("REFRESH_EXPIRED", "Refresh EXPIRED " +
                    "[ Time : " + LocalDateTime.now() +
                    " ]", null, null);
        }

        return new ReissueResponse("REFRESH_EXISTS", "Refresh EXISTS " +
                "[ Time : " + LocalDateTime.now() +
                " ]", resetAccessToken(request), reissueRefresh(request));
        // resetAccessToken : 새로운 access토큰을 생성하는 메소드
        // reissueRefresh : 새로운 refresh토큰을 생성하는 메소
    }

    private String resetAccessToken(HttpServletRequest request) {

        String refresh = findCookie(request);
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        return jwtUtil.createJwt("access", username, role, 600000L);

    }


    public String reissueRefresh(HttpServletRequest request) {

        String refresh = findCookie(request);
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
        refreshRepository.deleteByRefresh(refresh);

        addRefresh(username, newRefresh, 86400000L);
        return newRefresh;
    }

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }


    // 서버에 refresh 토큰을 저장하는 메소드
    public void addRefresh(String username, String refresh, Long expiredMs) {

        LocalDateTime localDateTime = LocalDateTime.now().plus(Duration.ofMillis(expiredMs));

        Refresh refreshToken = Refresh.builder()
                .username(username)
                .refresh(refresh)
                .expiration(localDateTime.toString())
                .build();

        refreshRepository.save(refreshToken);
    }

    public void zeroCookie(HttpServletResponse response){

        Cookie cookie = new Cookie("refresh",null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());

    }

    @Transactional
    public void deleteRefresh(String refresh){
        refreshRepository.deleteByRefresh(refresh);
    }
}
