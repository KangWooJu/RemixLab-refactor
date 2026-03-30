package org.woojukang.remixlab.global.utils.ai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRenderRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.response.direct.DirectPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoRenderResponse;
import org.woojukang.remixlab.global.client.ai.GptClient;
import org.woojukang.remixlab.global.client.ai.dto.request.AiClientRequest;
import org.woojukang.remixlab.global.client.ai.dto.request.photo.DALLERequest;
import org.woojukang.remixlab.global.client.ai.dto.request.plot.GptRequest;
import org.woojukang.remixlab.global.client.ai.dto.request.prompt.template.InitPromptTemplate;
import org.woojukang.remixlab.global.client.ai.dto.request.video.SoraRequest;
import org.woojukang.remixlab.global.client.ai.dto.response.photo.DALLEResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.plot.GptResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class AiUtils {

    private final GptClient gptClient;
    private final ObjectMapper objectMapper;


    @Value("${openai.model.video}")
    private String soraModel;

    // LLM API의 결과를 String 타입으로 변환
    public String getJsonText(String model,
                               AiClientRequest request) {

        GptResponse gptResponse = gptClient.sendMessage(
                new GptRequest(model, request.prompt())
        );

        return gptResponse.output()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(BaseExceptionEnum.LLM_API_RESPONSE_NOT_FOUND))
                .content()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BaseException(BaseExceptionEnum.LLM_API_RESPONSE_NOT_FOUND))
                .text();
    }




    // String타입의 json을 responseDTO에 매핑
    public <T> T responseParsing(String json,
                                 Class<T> responseType){
        try{
            return objectMapper.readValue(json,responseType);
        }catch(Exception e){
            throw new BaseException(BaseExceptionEnum.JSON_NOT_MATCHED);
        }
    }


    // DALLE API을 병렬 호출 ( 5회 )
    @Async("dalleExecutor")
    public CompletableFuture<InitPhotoRenderResponse> makePhotos
            (InitPhotoRenderRequest request){

        List<CompletableFuture<InitPhotoRenderResponse.Images>> futures =
                request.imagePrompts()
                        .stream()
                        .map(detail -> CompletableFuture.supplyAsync(()->{

                            DALLERequest dalleRequest = new DALLERequest(
                                    "gpt-image-1",
                                    detail.prompt(),
                                    "1024x1024"
                            );

                            DALLEResponse dalleResponse = gptClient.makeImage(dalleRequest);

                            return new InitPhotoRenderResponse.Images(
                                    String.valueOf(detail.sceneNumber()),
                                    dalleResponse.data().get(0).b64_json()
                            );

                        })).toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v->futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .thenApply(InitPhotoRenderResponse::new);

    }

    public DirectPhotoResponse makePhotoFromText(DirectPhotoRequest request){

        DALLERequest dalleRequest = new DALLERequest(
                "gpt-image-1",
                request.prompt(),  // 텍스트 입력
                "auto"
        );

        DALLEResponse dalleResponse = gptClient.makeImage(dalleRequest);

        // 한 개만 나오므로 data.get(0) 접근
        String base64Image = dalleResponse
                .data()
                .get(0)
                .b64_json();

        return new DirectPhotoResponse(base64Image);

    }

    public SoraResponse makeVideoFromPhotos(ShowPlotWithDetailResponse showPlotWithDetailResponse){


        String json;

        try {
            json = objectMapper.writeValueAsString(showPlotWithDetailResponse);
        } catch (JsonProcessingException e) {
            throw new BaseException(BaseExceptionEnum.JSON_NOT_MATCHED);
        }

        String prompt = InitPromptTemplate.INIT_VIDEO_PROMPT_MAKING
                .toString()
                .replace("{{input_json}}", json);

        SoraRequest soraRequest = new SoraRequest(soraModel,
                prompt,
                "12",
                "1280x720");

        return gptClient
                .makeVideo(soraRequest);

    }

    public SoraResponse makeVideoByText(DirectVideoRequest directVideoRequest){

        SoraRequest soraRequest = new SoraRequest(soraModel,
                directVideoRequest.prompt(),
                "12",
                "1280x720");

        return gptClient
                .makeVideo(soraRequest);

    }



}
