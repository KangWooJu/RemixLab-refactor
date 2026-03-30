package org.woojukang.remixlab.domain.creation.facade;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRenderRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPlotRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.response.direct.DirectPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.DirectPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.InitPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoRenderResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPlotResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitVideoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.plot.InitPlotResultResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.photo.entity.Photo;
import org.woojukang.remixlab.domain.creation.service.CreationService;
import org.woojukang.remixlab.domain.photo.service.PhotoService;
import org.woojukang.remixlab.domain.plot.service.PlotService;
import org.woojukang.remixlab.domain.quest.facade.QuestFacade;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.video.entity.Video;
import org.woojukang.remixlab.domain.video.service.VideoService;
import org.woojukang.remixlab.global.client.ai.dto.request.AiClientRequest;
import org.woojukang.remixlab.global.client.ai.dto.request.prompt.template.InitPromptTemplate;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.query.creation.dto.request.ShowPlotWithDetailRequest;
import org.woojukang.remixlab.query.creation.dto.response.ShowMyCreationResponse;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;
import org.woojukang.remixlab.query.creation.service.CreationQueryService;
import org.woojukang.remixlab.query.creation.service.PlotQueryService;
import org.woojukang.remixlab.query.user.service.UserQueryService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreationFacade {


    private final CreationService creationService;
    private final PlotService plotService;
    private final PhotoService photoService;
    private final VideoService videoService;

    private final UserQueryService userQueryService;
    private final PlotQueryService plotQueryService;
    private final CreationQueryService creationQueryService;

    private final QuestFacade questFacade;


    /* plot 생성기
    */

    @Transactional
    public InitPlotResultResponse makePlot
    (String username,
     InitPlotRequest initPlotRequest){

       // 프롬프트 결합해서 request DTO 생성하기
        AiClientRequest aiClientRequest =
                new AiClientRequest(String.format(
                        InitPromptTemplate
                                .INIT_PLOT_MAKING
                                .getTemplate()
                                .replace("{user_input}",
                                        initPlotRequest.user_input())));

        // creation 생성 후 , 저장
        Creation creation = creationService
                .InitCreation(userQueryService
                        .findByUsername(username),
                        initPlotRequest);

        // plot 생성하기
        InitPlotResponse initPlotResponse = creationService
                .initPlot(aiClientRequest);


        // plot 저장하기
        plotService
                .savePlot(creation,
                        initPlotResponse);

        // 퀘스트 여부 확인하기
        questFacade.onPlotCreated(username);

        // response 응답 생성하기
        return InitPlotResultResponse.from(creation.getId(),initPlotResponse);
    }

    /* photo 생성기
    */

    @Transactional
    public DirectPhotoResultResponse makePhotoDirectly
            (DirectPhotoRequest directPhotoRequest,
             String username){

        // 프롬프트 결합 후 , requestDTO 생성
        AiClientRequest aiClientRequest =
                new AiClientRequest(directPhotoRequest.prompt());

        // 사진 생성기 실행 ( 렌더링 X )
        DirectPhotoResponse directPhotoResponse =
                creationService.makePhotoFromText(directPhotoRequest);

        // User 가져오기
        User user = userQueryService
                .findByUsername(username);

        // creation 생성후 , photo 생성하기
        Creation creation = creationService
                .makeCreationDirect(user,
                        aiClientRequest);

        // photo 객체 저장 및 response 생성
        DirectPhotoResultResponse directPhotoResultResponse =
                photoService
                        .saveDirectPhoto(creation,
                                directPhotoResponse);

        // 퀘스트 체크
        questFacade.onPhotoCreated(username);

        return directPhotoResultResponse;
    }


    @Transactional
    public InitPhotoResultResponse makePhotos
    (InitPhotoRequest initPhotoRequest,
     String username) {

        // 프롬프트 결합후 , request DTO 생성
        AiClientRequest aiClientRequest =
                new AiClientRequest(String.format(
                        InitPromptTemplate
                                .INIT_IMAGE_PROMPT_MAKING
                                .getTemplate()
                                .replace("{user_input}",
                                        creationService.requestToString(initPhotoRequest))));

        // 사진 생성을 위한 프롬프트 생성기 실행 ( gpt API )
        InitPhotoResponse initPhotoResponse =
                creationService.initPhotos(aiClientRequest);

        // 사진 생성 프롬프트로 사진 생성하기 ( gpt API )
        InitPhotoRenderResponse initPhotoRenderResponse =  creationService
                .initPhotoRender(InitPhotoRenderRequest
                        .from(initPhotoResponse));


        // plot으로부터 creation 호출
        Creation creation = plotQueryService
                .findCreationByPlot(plotQueryService
                .findByTitle(initPhotoRequest.title()).getId());

        // creation 호출 후 , Photo 객체 생성후 저장하기
        List<Photo> photoList = photoService
                .savePhotos(creation,
                        initPhotoRenderResponse,
                        initPhotoRequest);

        // response 생성하기
        InitPhotoResultResponse initPhotoResultResponse = photoService.makeResult(photoList);


        return initPhotoResultResponse;
    }



    /* Video 생성하기
     */

    @Transactional
    public InitVideoResponse makeVideoByPhotos(InitVideoRequest initVideoRequest,String username){

        // Creation 가져오기
        Creation creation = creationQueryService
                .findCreationEntityById(initVideoRequest
                        .creationId());

        // plot 정보 가져오기
        ShowPlotWithDetailResponse showPlotWithDetailResponse =
                plotQueryService
                        .findPlotWithDetailsByCreationId(new ShowPlotWithDetailRequest(initVideoRequest
                                .creationId()));


        // 사진+플롯 정보를 프롬프트로 만든 후 , Video 생성하기
        SoraResponse soraResponse = creationService
                .makeVideoByPhotos(showPlotWithDetailResponse);

        // Video 객체 생성후 저장하기
        Video video = videoService.saveVideo(creation,soraResponse);

        // 퀘스트 체크
        questFacade.onVideoCreated(username);

        return new InitVideoResponse(video.getId(),
                soraResponse
                .id(),
                "비디오 생성이 접수되었습니다.");

    }

    @Transactional
    public InitVideoResponse makeVideoByText(String username,DirectVideoRequest directVideoRequest){

        User user = userQueryService.findByUsername(username);

        SoraResponse soraResponse =  creationService
                .makeVideoByText(directVideoRequest);

        AiClientRequest aiClientRequest = new AiClientRequest(directVideoRequest.prompt());

        // Creation 생성하기
        Creation creation = creationService
                .makeCreationDirect(user,aiClientRequest);

        // Video 객체 생성하기
        Video video = videoService.saveVideo(creation,soraResponse);

        // 퀘스트 체크
        questFacade.onVideoCreated(username);

        return new InitVideoResponse(video.getId(),
                soraResponse
                .id(),
                "비디오 생성이 접수되었습니다.");

    }



    /* 나의 모든 Creation 조회하기
     */
    public ShowMyCreationResponse showMyCreation(String username){

        return creationQueryService
                .findCreationByUserId(userQueryService
                        .findByUsername(username)
                        .getId());

    }

}
