package org.woojukang.remixlab.global.security.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woojukang.remixlab.global.security.dto.request.LoginRequest;

@RestController
@RequestMapping("/auth/docs")
@Tag(name = "유저 로그인 전용 스웨거",description = "해당 컨트롤러는 실제 API가 아닙니다.")
public class AuthDocsController {

    @Operation(summary = "로그인",description = "스프링 시큐리티 필터에 의해 로그인 처리됩니다. \n 실제 로그인 주소는 {호스트주소}:8080/login입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest){

    }
}
