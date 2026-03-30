package org.woojukang.remixlab.domain.quest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woojukang.remixlab.domain.quest.facade.QuestFacade;
import org.woojukang.remixlab.global.config.exception.dto.ApiResult;
import org.woojukang.remixlab.query.quest.dto.response.ShowQuestStatusResponse;

@RestController
@RequestMapping("/api/v1/quest")
@Tag(name = "퀘스트 API",description = "사용자의 퀘스트 완료여부를 확인합니다.")
@RequiredArgsConstructor
public class QuestController {

    private final QuestFacade questFacade;

    @Operation(summary = "회원 퀘스트 상태 API",description = "회원이 완수환 퀘스트를 반환합니다.")
    @GetMapping("/show")
    public ResponseEntity<ApiResult<ShowQuestStatusResponse>> showQuestStatus
            (@AuthenticationPrincipal UserDetails userDetails){

        return ResponseEntity
                .status(HttpStatus
                        .OK)
                .body(ApiResult
                        .success(questFacade
                                .showQuestStatus(userDetails
                                        .getUsername())));

    }


}
