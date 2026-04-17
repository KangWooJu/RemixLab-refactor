package org.woojukang.remixlab.global.client.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.woojukang.remixlab.global.client.ai.dto.request.photo.DALLERequest;
import org.woojukang.remixlab.global.client.ai.dto.request.plot.GptRequest;
import org.woojukang.remixlab.global.client.ai.dto.request.video.SoraRequest;
import org.woojukang.remixlab.global.client.ai.dto.response.photo.DALLEResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.plot.GptResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraStatusResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GptClient implements AiClient {

    private final WebClient openAIWebClient;

    @Value("${openai.url.gpt}")
    private String gptUrl;

    @Value("${openai.url.dalle}")
    private String dallEUrl;

    @Value("${openai.url.sora2}")
    private String soraUrl;

    @Value("${openai.url.soraStatus}")
    private String soraStatusUrl;

    @Value("${openai.url.soraDownload}")
    private String soraDownloadUrl;

    // 프롬프트 -> 텍스트
    public Mono<GptResponse> sendMessage(GptRequest request) {

        return openAIWebClient.post()
                .uri(gptUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GptResponse.class);
    }

    // 프롬프트 -> 사진
    public Mono<DALLEResponse> makeImage(DALLERequest request){

        return openAIWebClient.post()
                .uri(dallEUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DALLEResponse.class);
    }


    public Mono<SoraResponse> makeVideo(SoraRequest request){

        return openAIWebClient.post()
                .uri(soraUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SoraResponse.class);
    }

    public Mono<SoraStatusResponse> getVideoStatus(String videoId){

        return openAIWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(soraStatusUrl)
                        .build(videoId))
                .retrieve()
                .bodyToMono(SoraStatusResponse.class);
    }

    public Mono<byte[]> getVideo(String videoId){

        return openAIWebClient.get()
                .uri(soraDownloadUrl + videoId + "/content")
                .retrieve()
                .bodyToMono(byte[].class);
    }




}
