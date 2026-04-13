package org.woojukang.remixlab.global.security.integration;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.user.dto.request.UserCreateRequest;
import org.woojukang.remixlab.domain.user.repository.UserRepository;
import org.woojukang.remixlab.domain.user.service.UserService;
import org.woojukang.remixlab.global.security.util.JwtUtil;
import org.woojukang.remixlab.global.security.service.RefreshService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RefreshIntegrationTest {

    // 테스트 클래스 필드
    private String accessToken;
    private String refreshToken;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshService refreshService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {

        // 0. 테스트 전 삭제
        userRepository.deleteAll();

        // 1. 테스트용 유저 생성
        userService
                .create(new UserCreateRequest("testUser", "password"));

        // 2. MockMvc로 로그인 요청해서 쿠키와 헤더 세팅
        String loginRequestJson = """
        {
            "username": "testUser",
            "password": "password"
        }
    """;

        var result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        // 로그인 시 발급되는 access 헤더와 refresh 쿠키 저장
        accessToken = result.getResponse().getHeader("accessToken");

        var cookies = result.getResponse().getCookies();
        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                refreshToken = c.getValue();
            }
        }

        refreshService.addRefresh("testUser",refreshToken,600000L);

        System.out.println("=== BeforeEach Login ===");
        System.out.println("AccessToken: " + accessToken);
        System.out.println("RefreshToken: " + refreshToken);


    }



    @Test
    void reissue_success_whenValidRefreshCookieExists() throws Exception {

        // given
        String username = "testUser";

        String refreshToken = refreshService
                .getValue(username)
                .toString();

        System.out.println("=== Test Cookies ===");
        System.out.println("Refresh token to send: " + refreshToken);


        // when & then
        mockMvc.perform(
                        post("/api/v1/user/refresh/reissue")
                                .cookie(new Cookie("refreshToken", refreshToken))
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("access"))
                .andExpect(cookie().exists("refresh"));
    }

    @Test
    void reissue_fail_whenRefreshCookieIsMissing() throws Exception {

        mockMvc.perform(
                        post("/api/v1/user/refresh/reissue")
                                .header("Authorization", "Bearer " + accessToken)
                                .cookie(new Cookie("FAKE_TOKEN", "FAKE"))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }


}
