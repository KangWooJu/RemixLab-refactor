package org.woojukang.remixlab.domain.video.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.video.entity.Video;
import org.woojukang.remixlab.domain.video.entity.VideoStatus;
import org.woojukang.remixlab.domain.video.repository.VideoRepository;
import org.woojukang.remixlab.global.client.ai.GptClient;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.global.infra.GcsStorageUploader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final GptClient gptClient;
    private final GcsStorageUploader gcsStorageUploader;

    // Video 객체를 생성한 뒤 , save하는 메소드
    public Video saveVideo(Creation creation,
                           SoraResponse soraResponse) {

        Video video = Video
                .builder()
                .creation(creation)
                .soraVideoId(soraResponse.id())
                .videoStatus(VideoStatus.PENDING)
                .url(null)
                .build();

        videoRepository.save(video);

        return video;
    }

    public Flux<DataBuffer> getVideoUrl(String videoId) {

        return gptClient.getVideoStream(videoId);

    }

    // GCP Storage에 비디오 저장하기
    public Mono<String> uploadVideo(Flux<DataBuffer> videoStream){

        return DataBufferUtils.join(videoStream)
                .flatMap(buffer ->
                        Mono.fromCallable(() -> {

                            byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);

                            DataBufferUtils.release(buffer);

                            return gcsStorageUploader.videoUpload(
                                    new ByteArrayInputStream(bytes)
                            );

                        }).subscribeOn(Schedulers.boundedElastic())
                );
    }

    public void saveVideo(Video video) {
        videoRepository.save(video);
    }

}


