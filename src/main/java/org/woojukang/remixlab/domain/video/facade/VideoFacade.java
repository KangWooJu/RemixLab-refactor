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

        Video video = videoQueryService
                .findByCreationId(showVideoRequest
                        .creationId());

        // 요청해서 byte 코드로 가져오기 -> stream 변환후 GCS 저장 , URL 반환
        String url = videoService
                .uploadVideo(videoService.
                        getVideoUrl(video
                                .getSoraVideoId()));

        video.updateUrl(url);
        videoService.saveVideo(video);

        return new ShowVideoResponse(url);

    }
}
