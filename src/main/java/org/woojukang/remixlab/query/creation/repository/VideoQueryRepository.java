package org.woojukang.remixlab.query.creation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.creation.entity.QCreation;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.video.entity.QVideo;
import org.woojukang.remixlab.domain.video.entity.Video;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class VideoQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QVideo qVideo = QVideo.video;
    private final QCreation qCreation = QCreation.creation;

    public Video findById(Long videoId){

        return queryFactory
                .select(qVideo)
                .from(qVideo)
                .where(qVideo
                        .id
                        .eq(videoId))
                .fetchOne();
    }

    public Video findByCreationId(Long creationId){

        return queryFactory
                .select(qVideo)
                .from(qVideo)
                .where(qVideo
                        .creation
                        .id
                        .eq(creationId))
                .fetchOne();
    }

    public Integer countVideoByUser(User user){

        Long count = queryFactory
                .select(qVideo.count())
                .from(qVideo)
                .join(qVideo
                        .creation,
                        qCreation)
                .where(qCreation
                        .user.
                        eq(user))
                .fetchOne();

        return count != null ? count.intValue() : 0;

    }






}
