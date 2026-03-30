package org.woojukang.remixlab.global.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.video.worker.VideoWorker;

@Component
@RequiredArgsConstructor
public class VideoScheduler {

    private final VideoWorker videoWorker;

    @Scheduled(fixedDelay = 20000)
    public void runVideoPolling(){

        videoWorker.processPendingVideos();

    }
}
