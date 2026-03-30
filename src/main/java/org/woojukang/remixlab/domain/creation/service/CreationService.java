package org.woojukang.remixlab.domain.creation.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRenderRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPlotRequest;
import org.woojukang.remixlab.domain.creation.dto.response.direct.DirectPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoRenderResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPlotResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.creation.entity.CreationType;
import org.woojukang.remixlab.domain.creation.repository.CreationRepository;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.global.client.ai.dto.request.AiClientRequest;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.global.utils.ai.AiUtils;
import org.woojukang.remixlab.global.utils.creation.CreationUtils;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;

@Service
@RequiredArgsConstructor
public class CreationService {

    private final AiUtils aiUtils;
    private final CreationUtils creationUtils;
    private final CreationRepository creationRepository;

    @Value("${openai.model.plot}")
    private String plotModel;

    @Value("${openai.model.photo}")
    private String photoModel;

    @Value("${openai.model.video}")
    private String videoModel;

    /*
    PLOT 생성 메소드
     */



    public Creation makeCreationDirect(User user,AiClientRequest aiClientRequest){

        Creation creation = Creation
                .builder()
                .user(user)
                .creationType(CreationType.DIRECT)
                .userPrompt(aiClientRequest.prompt())
                .build();

        creationRepository.save(creation);

        return creation;
    }



    public Creation InitCreation(User user, InitPlotRequest initPlotRequest){
        // Creation 객체 생성
        Creation creation = Creation
                .builder()
                .user(user)
                .creationType(CreationType.INIT)
                .userPrompt(initPlotRequest.user_input())
                .build();

        // Creation 객체 저장하기
        creationRepository.save(creation);

        return creation;
    }



    public InitPlotResponse initPlot(AiClientRequest aiClientRequest){
        // Gpt API 호출
       return aiUtils
               .responseParsing(aiUtils
                                .getJsonText(plotModel,
                                        aiClientRequest),
                        InitPlotResponse.class);

    }


    /*
    PHOTO 생성 메소드
     */

    // 사진 렌더링에 맞는 형태로 변환하기
    public InitPhotoResponse initPhotos
    (AiClientRequest aiClientRequest){

        return aiUtils
                .responseParsing(aiUtils
                                .getJsonText(plotModel,
                                        aiClientRequest),
                        InitPhotoResponse.class);

    }

    // 가공 프롬프트 -> 사진 생성하기
    public InitPhotoRenderResponse initPhotoRender
            (InitPhotoRenderRequest request){
        return aiUtils
                .makePhotos(request)
                .join();
    }

    public DirectPhotoResponse makePhotoFromText(DirectPhotoRequest directPhotoRequest){
        return aiUtils
                .makePhotoFromText(directPhotoRequest);
    }


    /*
    VIDEO 생성 메소드
     */

    public SoraResponse makeVideoByPhotos(ShowPlotWithDetailResponse showPlotWithDetailResponse){
        return aiUtils
                .makeVideoFromPhotos(showPlotWithDetailResponse);
    }

    public SoraResponse makeVideoByText(DirectVideoRequest directVideoRequest){
        return aiUtils
                .makeVideoByText(directVideoRequest);
    }





    /*
    내부 매퍼 메소드
     */

    public String requestToString(InitPhotoRequest initPhotoRequest) {
       return creationUtils.photoRequestToString(initPhotoRequest);
    }





}
