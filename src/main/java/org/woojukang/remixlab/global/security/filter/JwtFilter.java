package org.woojukang.remixlab.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.user.repository.UserRepository;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;
import org.woojukang.remixlab.global.security.auth.UserDetailsImpl;
import org.woojukang.remixlab.global.security.util.JwtUtil;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return antPathMatcher.match("/login",path)||
                antPathMatcher.match("/api/v1/user/create",path)||
                antPathMatcher.match("/api/user/refresh/reissue",path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractToken(request);
        // 토큰이 비었는지 검사
        if(accessToken == null){
            log.info(" AccessToken NULL  [ Time : {} ]", LocalDateTime.now());
            filterChain.doFilter(request,response); // 다음 필터 진행
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try{
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e){
            handleExpiredJwt(response);
            return;
        }

        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            log.info("Token Invalid Category  [ Time : {} ]", LocalDateTime.now());
            handleInvalidTokenCategory(response,category);
            return;
        }

        // makeToken()메소드를 사용해서 토큰 생성 후
        SecurityContextHolder
                .getContext()
                .setAuthentication(makeToken(accessToken));

        filterChain
                .doFilter(request,response); // 다음 필터 진행
    }

    private String extractToken(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            return null;
        }
        return header.substring(7);
    }

    private Authentication makeToken(String token){
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> {
                    log.error("해당 유저 정보를 찾을 수 없습니다. username : {} [ Time : {} ]", username, LocalDateTime.now());
                    return new UsernameNotFoundException("해당 유저정보를 찾을 수 없습니다.");
                });


        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        // 토큰 생성 ( LoginFIlter 전 인증 생성 -> 이후에 LoginFilter에서는 username,password만 넣어 인증 요청 !! 둘이 파라미터 다름  )

        return new UsernamePasswordAuthenticationToken
                (userDetails,null,userDetails.getAuthorities());
    }

    // 토큰 만료 여부 확인
    private void handleExpiredJwt(HttpServletResponse response) throws IOException{

        ApiResult<?> result = ApiResult.fail("JWT_EXPIRED","JWT 토큰이 파기되었습니다.");

        String json = objectMapper.writeValueAsString(result);
        //response body
        response.getWriter().write(json);
        //response status code
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 상태코드 추가
        response.setContentType("application/json;charset=UTF-8"); // Content-Type 지정

    }

    // 토큰 카테고리 확인
    private void handleInvalidTokenCategory(HttpServletResponse response,
                                            String category) throws IOException{

        ApiResult<?> result = ApiResult
                .fail("INVALID_TOKEN_CATEGORY_NOT_FOUND",
                        "해당 토큰 카테고리 : "+category);

        String json = objectMapper.writeValueAsString(result);
        response.getWriter().write(json);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
    }

}
