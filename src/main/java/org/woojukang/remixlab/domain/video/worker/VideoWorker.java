package org.woojukang.remixlab.domain.video.worker;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.video.entity.Video;
import org.woojukang.remixlab.domain.video.entity.VideoStatus;
import org.woojukang.remixlab.domain.video.repository.VideoRepository;
import org.woojukang.remixlab.global.client.ai.GptClient;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraStatusResponse;

import java.util.List;

import static org.woojukang.remixlab.domain.video.entity.VideoStatus.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoWorker {

    private final VideoRepository videoRepository;
    private final GptClient gptClient;

    @Transactional
    public void processPendingVideos() {

        videoRepository.findByVideoStatusIn(
                List.of("queued", "in_progress")
        ).forEach(video -> {
            try {
                SoraStatusResponse res =
                        gptClient.getVideoStatus(video.getSoraVideoId());
                updateVideoStatus(video, res);
            } catch (Exception e) {
                log.error("Video status polling failed. videoId={}", video.getId(), e);
            }
        });
    }

    private void updateVideoStatus(Video video, SoraStatusResponse res) {

        String status = res.status().toLowerCase();

        switch (status) {

            case VideoStatus.COMPLETED -> {
                video.updateStatus(VideoStatus.COMPLETED);
                video.updateUrl(res.videoUrl());
            }

            case VideoStatus.FAILED -> {
                video.updateStatus(VideoStatus.FAILED);
            }

            case VideoStatus.PROCESSING,
                 VideoStatus.PENDING -> {
                video.updateStatus(VideoStatus.PROCESSING);
            }

            default -> {
                log.warn("Unknown status: {}", status);
            }
        }
    }
}
