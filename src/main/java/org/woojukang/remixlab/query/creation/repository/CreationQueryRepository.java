package org.woojukang.remixlab.query.creation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.creation.entity.QCreation;
import org.woojukang.remixlab.domain.photo.entity.QPhoto;
import org.woojukang.remixlab.domain.plot.entity.QPlot;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.video.entity.QVideo;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class CreationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QCreation qCreation = QCreation.creation;
    private final QPhoto qPhoto = QPhoto.photo;
    private final QVideo qVideo = QVideo.video;
    private final QPlot qPlot = QPlot.plot;
    // QEntity 나중에 삽입

    public List<Creation> findByUserId(Long userId){

        return jpaQueryFactory
                .selectFrom(qCreation)
                .where(qCreation
                        .user
                        .id
                        .eq(userId))
                .fetch();
    }

    public Creation findCreationEntityById(Long creationId){

        return jpaQueryFactory
                .select(qCreation)
                .from(qCreation)
                .where(qCreation
                        .id
                        .eq(creationId))
                .fetchOne();
    }

    public Long countCompletedProjects(User user){

        return jpaQueryFactory
                .select(qCreation.id.countDistinct())
                .from(qCreation)
                .leftJoin(qPlot).on(qPlot.creation.eq(qCreation))
                .leftJoin(qPhoto).on(qPhoto.creation.eq(qCreation))
                .leftJoin(qVideo).on(qVideo.creation.eq(qCreation))
                .where(
                        qCreation.user.eq(user),
                        qPlot.id.isNotNull(),
                        qPhoto.id.isNotNull(),
                        qVideo.id.isNotNull()
                )
                .fetchOne();

    }




}
