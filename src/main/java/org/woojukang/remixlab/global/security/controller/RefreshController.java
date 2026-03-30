package org.woojukang.remixlab.global.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;
import org.woojukang.remixlab.global.security.dto.response.ReissueResponse;
import org.woojukang.remixlab.global.security.service.RefreshService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/refresh")
@Tag(name = "refresh 토큰 관리", description = "유저의 refresh 토큰을 관리하는 도메인")
public class RefreshController {

    private final RefreshService refreshService;

    @Operation(summary = "refresh 토큰 갱신",description = "refresh 토큰을 갱신합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResult<ReissueResponse>> reissue(HttpServletRequest request,
                                                              HttpServletResponse response){

        ReissueResponse reissueResponse = refreshService.refreshCookies(request);

        System.out.println("Refresh status: " + reissueResponse.status());

        return switch (reissueResponse.status()) {
            case "REFRESH_EXISTS" -> {
                response
                        .setHeader("access",
                                reissueResponse
                                        .AccessToken());
                response
                        .addCookie(refreshService
                                .createCookie("refresh",
                                        refreshService
                                                .reissueRefresh(request)));
                yield new ResponseEntity<>(
                        ApiResult.success(reissueResponse),
                        HttpStatus.OK
                );
            }
            default -> new ResponseEntity<>(
                    ApiResult.fail("REISSUE_FAILED",
                            "토큰 갱신에 실패하였습니다."),
                    HttpStatus.BAD_REQUEST
            );
        };
    }


}
