package org.woojukang.remixlab.domain.photo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.woojukang.remixlab.domain.photo.dto.request.SelectPhotosRequest;
import org.woojukang.remixlab.query.creation.dto.response.PhotoResponse;
import org.woojukang.remixlab.domain.photo.facade.PhotoFacade;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;
import org.woojukang.remixlab.query.creation.dto.request.ShowPhotoRequest;
import org.woojukang.remixlab.query.creation.dto.request.ShowPhotoSelectedRequest;


@RestController
@RequestMapping("/api/v1/photo")
@RequiredArgsConstructor
@Tag(name = "사진 조회 API",description = "생성한 사진을 조회하는 API")
public class PhotoController {

    private final PhotoFacade photoFacade;


    @PostMapping("/select")
    @Operation(summary = "사진 선택하기",description = "Plot의 5가지 결과에 대해서 이미지 선택하기")
    public ResponseEntity<ApiResult<?>> selectPhotos
            (@RequestBody SelectPhotosRequest selectPhotosRequest,
             @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(photoFacade
                                .selectPhotos(selectPhotosRequest,
                                        userDetails
                                                .getUsername())));

    }


    // 지정한 SceneNumber에 대해서만 사진 조회
    @GetMapping("/view/selected")
    @Operation(summary = "사용자가 선택한 사진조회",
            description = "해당 creation 중, 사용자가 선택한 사진만 조회합니다.")
    public ResponseEntity<ApiResult<PhotoResponse>> showSelectedPhoto
    (@RequestBody ShowPhotoSelectedRequest showPhotoSelectedRequest,
     @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(photoFacade
                                .showPhotoSelected(showPhotoSelectedRequest,
                                        userDetails
                                                .getUsername())));
    }


    // 사용자의 모든 사진을 조회
    @GetMapping("/view/all")
    @Operation(summary = "사용자가 선택한 모든 사진 조회",
            description = "사용자가 지금까지 선택한 모든 사진이 조회됩니다.")
    public ResponseEntity<ApiResult<PhotoResponse>> showAllPhotos
    (@RequestBody ShowPhotoRequest showPhotoRequest,
     @AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(photoFacade
                                .showPhotoAll(showPhotoRequest)));
    }


}
