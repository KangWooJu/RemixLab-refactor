package org.woojukang.remixlab.global.utils.creation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRequest;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;
import org.woojukang.remixlab.query.creation.dto.response.ShowMyCreationResponse;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreationUtils {

    private static final int MAX_TITLE_LENGTH = 40; // 리스트 제목 최대 길이
    private final ObjectMapper objectMapper;

    public String photoRequestToString(InitPhotoRequest initPhotoRequest){
        try {
            return objectMapper.writeValueAsString(initPhotoRequest);
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseExceptionEnum.JSON_NOT_MATCHED);
        }
    }

    // 문자열 길이가 40이 넘어가면 짜르기
    public static ShowMyCreationResponse toShowResponse(List<Creation> creations) {
        List<ShowMyCreationResponse.CreationDetails> details =
                creations.stream()
                        .map(creation -> new ShowMyCreationResponse.CreationDetails(
                                creation.getId(),
                                Optional.ofNullable(creation.getUserPrompt())
                                        .map(String::trim)
                                        .filter(string -> !string.isBlank())
                                        .map(string -> string.length() <= MAX_TITLE_LENGTH // 문자열 길이가 40이 넘어가면 짜르기
                                                ? string
                                                : string.substring(0, MAX_TITLE_LENGTH - 3) + "...")
                                        .orElse("Untitled")
                        ))
                        .toList();

        return new ShowMyCreationResponse(details);
    }


}
