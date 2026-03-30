package org.woojukang.remixlab.domain.creation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPlotRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.response.photo.DirectPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.InitPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitVideoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.plot.InitPlotResultResponse;
import org.woojukang.remixlab.domain.creation.facade.CreationFacade;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;
import org.woojukang.remixlab.query.creation.dto.response.ShowMyCreationResponse;

@RestController
@RequestMapping("/api/v1/creation")
@RequiredArgsConstructor
@Tag(name = "생성 API",description = "생성 관련 API")
public class CreationController {

    private final CreationFacade creationFacade;

    // AI Plot 생성 API
    @PostMapping("/make/plot")
    @Operation(summary = "AI 플롯 생성",
            description = "AI를 통해 이야기 플롯을 생성합니다.")
    public ResponseEntity<ApiResult<InitPlotResultResponse>> makePlot
            (@RequestBody InitPlotRequest initPlotRequest,
             @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .CREATED)
                .body(ApiResult
                        .success(creationFacade
                                .makePlot(userDetails
                                                .getUsername(),
                                        initPlotRequest)));
    }

    // AI 사진 생성 API
    @PostMapping("/make/photo")
    @Operation(summary = "AI 사진 생성",
            description = "AI를 통해 사진을 생성합니다. 이때 , 플룻을 기반으로 생성합니다. \n /make/plot API의 호출 결과에서 creationId를 제외한 나머지를 그대로 requestBody에 넣어주시면 됩니다." )
    public ResponseEntity<ApiResult<InitPhotoResultResponse>> makePhoto
            (@RequestBody InitPhotoRequest initPhotoRequest,
             @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .CREATED)
                .body(ApiResult
                        .success(creationFacade
                                .makePhotos(initPhotoRequest,userDetails.getUsername())));

    }


    @PostMapping("/text/make/photo")
    @Operation(summary = "텍스트로 AI 사진생성",
            description = "AI를 통해 사진을 생성합니다. 이때 , 사용자의 입력을 기반으로 생성합니다.")
    public ResponseEntity<ApiResult<DirectPhotoResultResponse>> makePhotoByText
            (@RequestBody DirectPhotoRequest directPhotoRequest,
             @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity
                .status(HttpStatus
                        .CREATED)
                .body(ApiResult
                        .success(creationFacade
                                .makePhotoDirectly(directPhotoRequest,
                                        userDetails
                                                .getUsername())));

    }


    @PostMapping("/make/video")
    @Operation(summary = "비디오 생성기",
            description = "사용자가 선택한 사진을 기반으로 비디오를 생성합니다.\n이때 , 생성된 비디오의 url은 생성하지 않으며 비디오 생성을 요청만합니다. ")
    public ResponseEntity<ApiResult<InitVideoResponse>> makeVideo
    (@RequestBody InitVideoRequest initVideoRequest,
     @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .CREATED)
                .body(ApiResult
                        .success(creationFacade
                                .makeVideoByPhotos(initVideoRequest,
                                        userDetails.getUsername())));
    }

    @PostMapping("/text/make/video")
    @Operation(summary = "비디오 생성기",
            description = "텍스트 기반으로 비디오를 생성합니다.\n이때 , 생성된 비디오의 url은 생성하지 않으며 비디오 생성을 요청만합니다.")
    public ResponseEntity<ApiResult<InitVideoResponse>> makeVideoByText
            (@RequestBody DirectVideoRequest directVideoRequest,
             @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .CREATED)
                .body(ApiResult
                        .success(creationFacade
                                .makeVideoByText(userDetails
                                        .getUsername(),
                                        directVideoRequest)));
    }


    // 나의 creation 조회하기
    @GetMapping("/my")
    @Operation(summary = "나의 creation 조회",description = "내가 생성한 creation을 조회합니다.")
    public ResponseEntity<ApiResult<ShowMyCreationResponse>> showMyCreations
            (@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(creationFacade
                                .showMyCreation(userDetails
                                        .getUsername())));

    }

}
