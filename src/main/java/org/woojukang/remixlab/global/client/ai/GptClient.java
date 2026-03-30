package org.woojukang.remixlab.global.client.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.woojukang.remixlab.global.client.ai.dto.request.photo.DALLERequest;
import org.woojukang.remixlab.global.client.ai.dto.request.plot.GptRequest;
import org.woojukang.remixlab.global.client.ai.dto.request.video.SoraRequest;
import org.woojukang.remixlab.global.client.ai.dto.response.photo.DALLEResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.plot.GptResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraStatusResponse;

@Component
@RequiredArgsConstructor
public class GptClient implements AiClient {

    private final RestTemplate openAIRestTemplate;

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
    public GptResponse sendMessage
    (GptRequest request) {

        return openAIRestTemplate
                .postForObject(gptUrl,
                        request,
                        GptResponse.class);
    }

    // 프롬프트 -> 사진
    public DALLEResponse makeImage
    (DALLERequest request){

        return openAIRestTemplate
                .postForObject(dallEUrl,
                        request,
                        DALLEResponse.class);
    }


    public SoraResponse makeVideo
            (SoraRequest request){
        return openAIRestTemplate
                .postForObject(soraUrl,
                        request,
                        SoraResponse.class);
    }

    public SoraStatusResponse getVideoStatus
            (String videoId){

        return openAIRestTemplate
                .getForObject(soraStatusUrl,
                        SoraStatusResponse.class,
                        videoId);

    }

    public byte[] getVideo(String videoId){

       return openAIRestTemplate.getForObject(
               soraDownloadUrl + videoId + "/content",
               byte[].class
       );

    }




}
