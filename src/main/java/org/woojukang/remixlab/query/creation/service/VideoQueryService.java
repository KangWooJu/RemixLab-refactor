package org.woojukang.remixlab.query.creation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.video.entity.Video;
import org.woojukang.remixlab.query.creation.repository.VideoQueryRepository;

@Service
@RequiredArgsConstructor
public class VideoQueryService {

    private final VideoQueryRepository videoQueryRepository;

    public Video findById(Long videoId){
        return videoQueryRepository
                .findById(videoId);
    }

    public Video findByCreationId(Long creationId){

        return videoQueryRepository
                .findByCreationId(creationId);
    }

    public Integer countVideoByUser(User user){

        return videoQueryRepository
                .countVideoByUser(user);
    }

}
