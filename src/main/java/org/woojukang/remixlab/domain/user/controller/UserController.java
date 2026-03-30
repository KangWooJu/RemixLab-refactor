package org.woojukang.remixlab.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.woojukang.remixlab.domain.user.dto.request.UserCreateRequest;
import org.woojukang.remixlab.domain.user.dto.request.UserInfoRequest;
import org.woojukang.remixlab.domain.user.dto.response.UserCreateResponse;
import org.woojukang.remixlab.domain.user.dto.response.UserInfoResponse;
import org.woojukang.remixlab.domain.user.facade.UserFacade;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;

@Tag(name = "User",description = "유저 정보 API")
@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    /*
    Command 영역
     */

    @Operation(summary = "유저 생성",description = "유저를 생성합니다.")
    @PostMapping("/create")
    public ResponseEntity<ApiResult<UserCreateResponse>> create
            (@RequestBody UserCreateRequest userCreateRequest){

        return ResponseEntity
                .status(HttpStatus
                        .CREATED)
                .body(ApiResult
                        .success(userFacade
                                .create(userCreateRequest)));

    }

    /*
    Query 영역
     */

    @Operation(summary = "유저 정보",description = "유저 정보 가져오기")
    @GetMapping("/info")
    public ResponseEntity<ApiResult<UserInfoResponse>> info
            (@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult
                        .success(userFacade
                                .userInfo(userDetails.getUsername())));

    }

    
}
