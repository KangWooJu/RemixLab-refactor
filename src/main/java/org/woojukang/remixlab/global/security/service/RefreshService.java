package org.woojukang.remixlab.global.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;
import org.woojukang.remixlab.global.security.dto.response.ReissueResponse;
import org.woojukang.remixlab.global.security.repository.RefreshRepository;
import org.woojukang.remixlab.global.security.util.CookieUtil;
import org.woojukang.remixlab.global.security.util.JwtUtil;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CookieUtil cookieUtil;


    // refresh 토큰 기반으로 access , refresh 토큰을 재갱신하는 메소드
    public ReissueResponse refreshCookies(HttpServletRequest request) {

        String refresh = cookieUtil.findCookie(request);
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

    // access 토큰을 초기화 하는 메소드
    private String resetAccessToken(HttpServletRequest request) {

        String refresh = cookieUtil.findCookie(request);
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        return jwtUtil.createJwt("access",
                username,
                role,
                600000*6*24L);

    }

    // Refresh 토큰을 갱신하는 메소드
    public String reissueRefresh(HttpServletRequest request) {

        String refresh = cookieUtil.findCookie(request);
        String username = jwtUtil.getUsername(refresh);

        String newRefresh = jwtUtil
                .createJwt("refresh",
                        username,
                        jwtUtil.getRole(refresh),
                        7*600000*6*24L);

        // 기존의 refresh 토큰 삭제
        deleteRefresh(refresh);
        // 새로운 refresh 토큰 추가
        addRefresh(username, newRefresh, 7*600000*6*24L);

        return newRefresh;
    }

    // 쿠키를 생성하는 로직
    public Cookie createCookie(String key, String value) {

        return cookieUtil
                .createCookie(key,value);
    }

    // 서버에 refresh 토큰을 저장하는 메소드
    public void addRefresh(String username, String refresh, Long expiredMs) {

        // username 기반으로 저장
        refreshRepository
                .save(username,
                        refresh,
                        expiredMs);

        // refresh 기반으로 저장
        refreshRepository
                .save(refresh,
                        username,
                        expiredMs);
    }

    // 서버에서 refresh 토큰을 삭제
    public void deleteRefresh(String refresh){

        String username = getValue(refresh).toString();

        // 로그아웃 상태 확인
        if(username == null){
            return;
        }

        // refresh 기반의 value 삭제
        refreshRepository
                .delete(refresh);

        // username 기반의 value 삭제
        refreshRepository
                .delete(username);

    }

    public Object getValue(String key){

        return refreshRepository
                .findByKey(key);
    }


    public void validateAlreadyLogin(String username){

       if(refreshRepository.exists(username)){
           throw new BaseException(BaseExceptionEnum.USER_ALREADY_LOGIN);
       }
    }
}
