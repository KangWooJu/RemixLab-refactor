package org.woojukang.remixlab.domain.plot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woojukang.remixlab.domain.plot.facade.PlotFacade;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;
import org.woojukang.remixlab.query.creation.dto.request.ShowPlotRequest;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/plot")
@RequiredArgsConstructor
@Tag(name = "플롯 조회 API",description = "생성된 플롯을 조회하는 API")
public class PlotController {

    private final PlotFacade plotFacade;

    // 단일 Creation에 대한 플롯 조회
    @GetMapping("/view")
    @Operation(summary = "Creation에 대한 플롯을 조회",
            description = "하나의 Creation에 대해서 플롯을 조회하는 API")
    public ResponseEntity<ApiResult<ShowPlotResponse>> showPlot
            (@RequestBody ShowPlotRequest showPlotRequest, // requestDTO 생성 후 변경필요
             @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult
                        .success(plotFacade
                                .showPlot(showPlotRequest
                                        .creationId())));

    }


}
