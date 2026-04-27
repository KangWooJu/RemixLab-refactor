package org.woojukang.remixlab.domain.creation.facade;

import io.micrometer.core.instrument.Timer;
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
import org.woojukang.remixlab.domain.video.facade.VideoPersistenceFacade;
import org.woojukang.remixlab.domain.video.service.VideoService;
import org.woojukang.remixlab.global.client.ai.dto.request.AiClientRequest;
import org.woojukang.remixlab.global.client.ai.dto.request.prompt.template.InitPromptTemplate;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.global.metrics.PhotoGenerationMetrics;
import org.woojukang.remixlab.global.metrics.VideoGenerationMetrics;
import org.woojukang.remixlab.query.creation.dto.request.ShowPlotWithDetailRequest;
import org.woojukang.remixlab.query.creation.dto.response.ShowMyCreationResponse;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;
import org.woojukang.remixlab.query.creation.service.CreationQueryService;
import org.woojukang.remixlab.query.creation.service.PlotQueryService;
import org.woojukang.remixlab.query.user.service.UserQueryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

    private final PhotoGenerationMetrics photoGenerationMetrics;
    private final VideoGenerationMetrics videoGenerationMetrics;

    private final CreationPersistenceFacade creationPersistenceFacade;
    private final VideoPersistenceFacade videoPersistenceFacade;


    /* plot 생성기
    */

    public Mono<InitPlotResultResponse> makePlotReactive
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

        return creationService.initPlot(aiClientRequest)
                .flatMap(initPlotResponse ->
                        Mono.fromCallable(() ->
                                creationPersistenceFacade.savePlotBlocking(
                                        username,
                                        initPlotRequest,
                                        initPlotResponse
                                )
                        ).subscribeOn(Schedulers.boundedElastic())
                );
    }


    /* photo 생성기
    */

    // Text 기반 사진 생성
    public Mono<DirectPhotoResultResponse> makePhotoDirectlyReactive(
            DirectPhotoRequest directPhotoRequest,
            String username) {

        // 플로우 구분 : direct
        String flow = "direct";

        // 현재 처리 중인 요청 수 증가
        photoGenerationMetrics.incrementInflight(flow);

        // 전체 요청의 end-to-end latency 측정 시작
        Timer.Sample totalSample = photoGenerationMetrics.start();

        AiClientRequest aiClientRequest =
                new AiClientRequest(directPhotoRequest.prompt());

        // 텍스트 기반 사진 생성 기능의 호출 latency 측정 시작
        Timer.Sample generationSample = photoGenerationMetrics.start();


        return creationService.makePhotoFromText(directPhotoRequest)
                // 텍스트 기반 사진 생성 성공 시 , generate 단계의 latency 기록
                .doOnSuccess(res -> photoGenerationMetrics.stopStage(generationSample, flow, "generate"))
                .flatMap(directPhotoResponse -> {
                    Timer.Sample persistenceSample = photoGenerationMetrics.start();

                    return Mono.fromCallable(() ->
                                    creationPersistenceFacade.saveDirectPhotoBlocking(
                                            directPhotoResponse,
                                            username,
                                            aiClientRequest
                                    )
                            )
                            // blocking 작업에 대해서는 boundedElastic 스레드풀로 분리
                            .subscribeOn(Schedulers.boundedElastic())
                            // 저장 성공 시 , persistence 단계 latency 기록
                            .doOnSuccess(res -> photoGenerationMetrics.stopStage(persistenceSample, flow, "persistence"));
                })
                // 전체 요청 성공 건수 증가
                .doOnSuccess(result -> photoGenerationMetrics.incrementRequest(flow, "success"))
                // 전체 요청 실패 건수 증가
                .doOnError(e -> photoGenerationMetrics.incrementRequest(flow, "fail"))
                .doFinally(signal -> {
                    photoGenerationMetrics.stopTotal(totalSample, flow); // 전체 end-to-end latency 기록
                    photoGenerationMetrics.decrementInflight(flow); // 현재 처리 중 요청 수 감소
                });
    }




    // Plot 기반 사진 생성
    public Mono<InitPhotoResultResponse> makePhotosReactive(
            InitPhotoRequest initPhotoRequest,
            String username) {

        // 플로우 구분 : plot
        String flow = "plot";

        // 현재 처리 중인 요청 수 증가
        photoGenerationMetrics.incrementInflight(flow);

        // 전체 요청의 end-to-end latency 측정 시작
        Timer.Sample totalSample = photoGenerationMetrics.start();

        AiClientRequest aiClientRequest =
                new AiClientRequest(
                        InitPromptTemplate.INIT_IMAGE_PROMPT_MAKING
                                .getTemplate()
                                .replace("{user_input}",
                                        creationService.requestToString(initPhotoRequest))
                );

        // 이미지 생성용 데이터렌더링 로직 latency 측정 시작
        Timer.Sample initSample = photoGenerationMetrics.start();


        return creationService.initPhotos(aiClientRequest)
                // 렌더링 단계 latency 기록
                .doOnSuccess(res -> photoGenerationMetrics.stopStage(initSample, flow, "init"))
                .flatMap(initPhotoResponse -> {
                    // 이미지로 변환 하는 로직 latency 측정 시작
                    Timer.Sample renderSample = photoGenerationMetrics.start();

                    return creationService.initPhotoRender(
                                    InitPhotoRenderRequest.from(initPhotoResponse)
                            )
                            // 랜더링 성공 시 latency 기록
                            .doOnSuccess(res -> photoGenerationMetrics.stopStage(renderSample, flow, "render"));
                })
                .flatMap(initPhotoRenderResponse -> {
                    // persistence 단계 latency 측정 시작
                    Timer.Sample persistenceSample = photoGenerationMetrics.start();

                    return Mono.fromCallable(() ->
                                    creationPersistenceFacade.saveInitPhotosBlocking(
                                            initPhotoRenderResponse,
                                            initPhotoRequest
                                    )
                            )
                            // JPA 로직은 boundedElastic 스레드 내부에서 처리
                            .subscribeOn(Schedulers.boundedElastic())
                            // JPA 로직 성공 시 , persistence 단계 latency 기록
                            .doOnSuccess(res -> photoGenerationMetrics.stopStage(persistenceSample, flow, "persistence"));
                })
                // 전체 성공 건수 증가
                .doOnSuccess(result -> photoGenerationMetrics.incrementRequest(flow, "success"))
                .doOnError(e -> photoGenerationMetrics.incrementRequest(flow, "fail"))
                // 마지막에 항상 전체 latency 기록 및 in-flight 감소
                .doFinally(signal -> {
                    photoGenerationMetrics.stopTotal(totalSample, flow);
                    photoGenerationMetrics.decrementInflight(flow);
                });
    }


    /*
    Video 생성하기
     */


    public Mono<InitVideoResponse> makeVideoByPhotosReactive(
            InitVideoRequest initVideoRequest,
            String username) {
        String flow = "photo_based";

        videoGenerationMetrics.incrementInflight(flow);
        Timer.Sample totalSample = videoGenerationMetrics.start();

        Timer.Sample querySample = videoGenerationMetrics.start();

        return Mono.fromCallable(() ->
                        videoPersistenceFacade.findPlotDetailsBlocking(initVideoRequest.creationId())
                )
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(res -> videoGenerationMetrics.stopStage(querySample, flow, "query_plot"))

                .flatMap(showPlotWithDetailResponse -> {
                    Timer.Sample soraSample = videoGenerationMetrics.start();

                    return creationService.makeVideoByPhotosReactive(showPlotWithDetailResponse)
                            .doOnSuccess(res -> videoGenerationMetrics.stopStage(soraSample, flow, "generate_video"));
                })

                .flatMap(soraResponse -> {
                    Timer.Sample persistenceSample = videoGenerationMetrics.start();

                    return Mono.fromCallable(() ->
                                    videoPersistenceFacade.saveVideoByPhotosBlocking(
                                            initVideoRequest,
                                            username,
                                            soraResponse
                                    )
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSuccess(res -> videoGenerationMetrics.stopStage(
                                    persistenceSample,
                                    flow,
                                    "persistence"
                            ));
                })

                .doOnSuccess(result -> videoGenerationMetrics.incrementRequest(flow, "success"))
                .doOnError(e -> videoGenerationMetrics.incrementRequest(flow, "fail"))
                .doFinally(signal -> {
                    videoGenerationMetrics.stopTotal(totalSample, flow);
                    videoGenerationMetrics.decrementInflight(flow);
                });
    }

    public Mono<InitVideoResponse> makeVideoByTextReactive(
            String username,
            DirectVideoRequest directVideoRequest) {

        return creationService.makeVideoByTextReactive(directVideoRequest)
                .flatMap(soraResponse ->
                        Mono.fromCallable(() ->
                                videoPersistenceFacade.saveVideoByTextBlocking(
                                        username,
                                        directVideoRequest,
                                        new AiClientRequest(directVideoRequest.prompt()),
                                        soraResponse
                                )
                        ).subscribeOn(Schedulers.boundedElastic())
                );
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
