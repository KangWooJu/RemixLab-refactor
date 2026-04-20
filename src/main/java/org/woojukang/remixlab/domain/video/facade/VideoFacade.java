package org.woojukang.remixlab.domain.video.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.video.dto.request.ShowVideoRequest;
import org.woojukang.remixlab.domain.video.dto.response.ShowVideoResponse;
import org.woojukang.remixlab.domain.video.dto.response.VideoStatusResponse;
import org.woojukang.remixlab.domain.video.entity.Video;
import org.woojukang.remixlab.domain.video.entity.VideoStatus;
import org.woojukang.remixlab.domain.video.service.VideoService;
import org.woojukang.remixlab.query.creation.service.VideoQueryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VideoFacade {

    private final VideoQueryService videoQueryService;
    private final VideoService videoService;


    public VideoStatusResponse getVideoStatus(Long videoId){

        Video video = videoQueryService.findById(videoId);

        return new VideoStatusResponse(
                video.getId(),
                video.getVideoStatus(),
                video.getUrl()
        );
    }

    @Transactional
    public ShowVideoResponse getShowVideo
            (ShowVideoRequest showVideoRequest){

        return getShowVideoReactive(showVideoRequest)
                .block();

    }

    @Transactional
    public Mono<ShowVideoResponse> getShowVideoReactive(
            ShowVideoRequest showVideoRequest){

        Video video = videoQueryService
                .findByCreationId(showVideoRequest.creationId());

        return videoService
                .getVideoUrl(video.getSoraVideoId())
                .as(videoService::uploadVideo)
                .flatMap(url ->
                        Mono.fromCallable(() -> {

                            video.updateUrl(url);
                            videoService.saveVideo(video);

                            return url;

                        }).subscribeOn(Schedulers.boundedElastic()) // blocking 메소드는 boundedElastic()처리
                )
                .map(ShowVideoResponse::new);
    }
}
