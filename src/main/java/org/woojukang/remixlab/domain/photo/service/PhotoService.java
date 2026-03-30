package org.woojukang.remixlab.domain.photo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.photo.dto.request.SelectPhotosRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.response.direct.DirectPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.DirectPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.InitPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoRenderResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.photo.entity.Photo;
import org.woojukang.remixlab.domain.photo.entity.PhotoSelection;
import org.woojukang.remixlab.domain.photo.repository.PhotoRepository;
import org.woojukang.remixlab.domain.photo.repository.PhotoSelectionRepository;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.global.infra.CloudStorageUploader;
import org.woojukang.remixlab.global.utils.app.AppUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoSelectionRepository photoSelectionRepository;
    private final CloudStorageUploader cloudStorageUploader;


    public DirectPhotoResultResponse saveDirectPhoto
            (Creation creation,
             DirectPhotoResponse directPhotoResponse){

        byte[] decoded = decodeBase64(directPhotoResponse.images_base64());

        String url = cloudStorageUploader.upload(decoded, "image/png");

        Photo photo = Photo.builder()
                .creation(creation)
                .url(url)
                .sceneNumber(0)
                .title(AppUtils
                        .truncateOrDefault(creation
                                .getUserPrompt()))
                .build();

        photoRepository.save(photo);

        return new DirectPhotoResultResponse(url);

    }


    // 사진을 디코딩 한 후 , 외부저장소에 저장 및 DB에 URL을 저장하는 로직
    public List<Photo> savePhotos(Creation creation,
                           InitPhotoRenderResponse response,
                           InitPhotoRequest initPhotoRequest) {

        // SceneNumber + Title 조합을 requestDTO에서 추출하기
        Map<Integer, String> sceneMap = initPhotoRequest.scenes()
                .stream()
                .collect(Collectors.toMap(
                        InitPhotoRequest.Scene::sceneNumber,
                        InitPhotoRequest.Scene::sceneDescription
                ));

        // 리스트 타입으로 묶어서 저장하기
        List<Photo> photos = response
                .images()
                .stream()
                .map(img -> {
                    byte[] decoded = decodeBase64(img.imageBase64()); // 사진 디코딩

                    String url = cloudStorageUploader.upload(decoded, "image/png"); // Cloud Storage에 사진 저장

                    Integer sceneNumber = Integer
                            .parseInt(img.sceneNumber());

                    return Photo.builder() // Cloud Storage 저장을 통해 얻은 url을 저장
                            .creation(creation)
                            .sceneNumber(sceneNumber)
                            .title(sceneMap
                                    .getOrDefault(sceneNumber,
                                            "scene " + sceneNumber))
                            .url(url)
                            .build();
                })
                .toList();

        photoRepository.saveAll(photos);

        return photos;
    }

    // 사진 생성 후 , 프론트 전용 response 생성하기
    public InitPhotoResultResponse makeResult(List<Photo> photoList){

        List<InitPhotoResultResponse.PhotoDetail> result = photoList
                .stream()
                .map(photo -> new InitPhotoResultResponse.PhotoDetail(
                        photo.getSceneNumber(),
                        photo.getTitle(),
                        photo.getUrl()
                ))
                .toList();

        return new InitPhotoResultResponse(result);

    }


    // Base64 형태의 바이트 코드를 디코딩
    private byte[] decodeBase64(String base64) {
        if (base64.contains(",")) {
            base64 = base64.split(",")[1]; // data:image/png;base64 제거
        }
        return Base64.getDecoder().decode(base64);
    }



    // 선택한 사진 저장하기
    public List<PhotoSelection> saveSelections(User user, Creation creation, SelectPhotosRequest request) {

        List<PhotoSelection> newSelections = request.selections().stream()
                .filter(s -> !photoSelectionRepository.existsByUserIdAndCreationIdAndSceneNumber(
                        user.getId(), creation.getId(), s.sceneNumber()))
                .map(s -> PhotoSelection.builder()
                        .userId(user.getId())
                        .creation(creation)
                        .sceneNumber(s.sceneNumber())
                        .build())
                .toList();

        return photoSelectionRepository.saveAll(newSelections);
    }



}
