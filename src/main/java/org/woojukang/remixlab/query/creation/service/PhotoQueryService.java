package org.woojukang.remixlab.query.creation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.query.creation.dto.response.PhotoResponse;
import org.woojukang.remixlab.domain.photo.entity.Photo;
import org.woojukang.remixlab.query.creation.dto.request.ShowPhotoRequest;
import org.woojukang.remixlab.query.creation.dto.request.ShowPhotoSelectedRequest;
import org.woojukang.remixlab.query.creation.repository.PhotoQueryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoQueryService {

    private final PhotoQueryRepository photoQueryRepository;

    // 해당 creation에 대해서 모든 photo를 가져오는 메소드
    public PhotoResponse showPhotos
    (ShowPhotoRequest showPhotoRequest) {

        List<Photo> photos = photoQueryRepository
                .findByCreationId(showPhotoRequest
                .creationId());

        List<PhotoResponse.photoDetails> details = photos
                .stream()
                .map(photo -> new PhotoResponse.photoDetails(
                        photo.getSceneNumber(),  // sceneNumber
                        photo.getUrl()           // Photo URL
                ))
                .toList();

        return new PhotoResponse(details);
    }

    // 해당 creation에 대해서 해금된 photo를 가져오는 메소드
    public PhotoResponse showPhotosOnlSelected
            (ShowPhotoSelectedRequest showPhotoSelectedRequest,Long userId){

        List<Photo> photos = photoQueryRepository
                .findPhotosSelectedByCreationId(showPhotoSelectedRequest
                                .creationId(),
                        userId);

        List<PhotoResponse.photoDetails> details = photos
                .stream()
                .map(photo -> new PhotoResponse.photoDetails(
                        photo.getSceneNumber(),  // sceneNumber
                        photo.getUrl()           // Photo URL
                ))
                .toList();

        return new PhotoResponse(details);
    }

    public Integer countSelectedPhotosByUser(User user){

        return photoQueryRepository.countSelectedPhotosByUser(user);
    }


}
