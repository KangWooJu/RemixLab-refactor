package org.woojukang.remixlab.domain.video.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.woojukang.remixlab.domain.video.dto.request.ShowVideoRequest;
import org.woojukang.remixlab.domain.video.dto.response.ShowVideoResponse;
import org.woojukang.remixlab.domain.video.dto.response.VideoStatusResponse;
import org.woojukang.remixlab.domain.video.facade.VideoFacade;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;

@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
@Tag(name = "비디오 API",
        description = "생성된 비디오를 조회합니다.")
public class VideoController {

    private final VideoFacade videoFacade;

    @GetMapping("/retrieve/status/{videoId}")
    @Operation(summary = "비디오 조회상태 검색",
            description = "SORA API호출 후 , 비디오 완성 상태를 조회합니다. \n생성 완료시, /show API를 통해 url을 얻을 수 있습니다.")
    public ResponseEntity<ApiResult<VideoStatusResponse>> retrieveVideoStatus
            (@PathVariable Long videoId){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(videoFacade
                                .getVideoStatus(videoId)));

    }

    @GetMapping("/show")
    @Operation(summary = "비디오 조회",
            description = "생성된 비디오를 조회합니다.")
    public ResponseEntity<ApiResult<ShowVideoResponse>> showVideo
            (@RequestBody ShowVideoRequest showVideoRequest){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(videoFacade
                                .getShowVideo(showVideoRequest)));

    }


}
