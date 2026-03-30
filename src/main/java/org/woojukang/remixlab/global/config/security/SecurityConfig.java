package org.woojukang.remixlab.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.woojukang.remixlab.domain.user.repository.UserRepository;
import org.woojukang.remixlab.global.security.filter.JwtFilter;
import org.woojukang.remixlab.global.security.filter.JwtLogoutFilter;
import org.woojukang.remixlab.global.security.filter.LoginFilter;
import org.woojukang.remixlab.global.security.service.RefreshService;
import org.woojukang.remixlab.global.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final AntPathMatcher antPathMatcher;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final RefreshService refreshService;


    @Bean
    public LoginFilter loginFilter
            (AuthenticationManager authenticationManager)
            throws Exception {
        return new LoginFilter(objectMapper,
                authenticationManager,
                jwtUtil,refreshService );
    }

    @Bean
    public JwtLogoutFilter jwtlogoutFilter(){
        return new JwtLogoutFilter(jwtUtil,refreshService);
    }

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter(jwtUtil,userRepository,objectMapper,antPathMatcher);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityfilterChain(HttpSecurity httpSecurity,
                                                   LoginFilter loginFilter)
            throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable);

        httpSecurity
                .formLogin(AbstractHttpConfigurer::disable);

        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable);

        httpSecurity
                .addFilterAt(loginFilter,
                        UsernamePasswordAuthenticationFilter.class); // 필터 순서 2 ( 로그인 )
        httpSecurity
                .addFilterBefore(jwtFilter(), LoginFilter.class); // 필터 순서 1 ( 로그인 )
        httpSecurity
                .addFilterBefore(jwtlogoutFilter(), LogoutFilter.class); // 필터순서 1 ( 로그아웃 )

        // CORS 설정
        httpSecurity
                .cors((corsCustomizer)->corsCustomizer.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(List.of(
                            "http://localhost:3000",
                            "http://localhost:5173",
                            "https://remixlab-frontend.vercel.app"
                    ));
                    configuration.setAllowedMethods(Collections.singletonList("*")); // HTTP 메소드 지정
                    configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청을 허용
                    configuration.setAllowedHeaders(Collections.singletonList("*")); // 클라이언트 요청 시 보낼 수 있는 헤더 지정
                    configuration.setMaxAge(3600L); // 브라우저 preflight 요청캐싱 시간 지정
                    configuration.setExposedHeaders(List.of("AccessToken","accesstoken"));

                    return configuration;
                }));
        // 세션을 유지하지 않도록 하는 설정 -> STATELESS
        httpSecurity
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Http 주소허용 여부 설정 -> Default
        httpSecurity
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/login","/api/v1/user/create").permitAll()
                        .requestMatchers("/refresh").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }




}
