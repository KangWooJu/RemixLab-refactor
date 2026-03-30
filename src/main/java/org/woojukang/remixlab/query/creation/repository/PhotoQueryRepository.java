package org.woojukang.remixlab.query.creation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.photo.entity.Photo;
import org.woojukang.remixlab.domain.photo.entity.QPhoto;
import org.woojukang.remixlab.domain.photo.entity.QPhotoSelection;
import org.woojukang.remixlab.domain.user.entity.User;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhotoQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPhoto qPhoto = QPhoto.photo;
    private final QPhotoSelection qPhotoSelection = QPhotoSelection.photoSelection;


    // creationId 기반 Photo 조회
    public List<Photo> findByCreationId(Long creationId) {
        return jpaQueryFactory
                .selectFrom(qPhoto)
                .where(qPhoto.creation.id.eq(creationId))
                .fetch();
    }


    // 단일 Creation에 대해서 사용자가 해금한 사진들만 조회하는 메소드
    public List<Photo> findPhotosSelectedByCreationId(Long creationId,
                                                      Long userId){

        return jpaQueryFactory
                .selectFrom(qPhoto)
                .join(qPhotoSelection)
                .on(
                        qPhoto.creation.id.eq(qPhotoSelection.creation.id)
                                .and(qPhoto.sceneNumber.eq(qPhotoSelection.sceneNumber))
                )
                .where(
                        qPhotoSelection.userId.eq(userId),
                        qPhotoSelection.creation.id.eq(creationId)
                )
                .fetch();
    }

    // 사용자가 택한 모든 사진들을 가져오는 메소드
    public List<Photo> findPhotosSelectedByUserId(Long userId){
        return jpaQueryFactory
                .selectFrom(qPhoto)
                .join(qPhotoSelection)
                .on(
                        qPhoto.creation.id.eq(qPhotoSelection.creation.id)
                                .and(qPhoto.sceneNumber.eq(qPhotoSelection.sceneNumber))
                )
                .where(qPhotoSelection.userId.eq(userId))
                .fetch();
    }

    public Integer countSelectedPhotosByUser(User user){

        Long count = jpaQueryFactory
                .select(qPhotoSelection.count())
                .from(qPhotoSelection)
                .where(qPhotoSelection.userId.eq(user.getId()))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

}
