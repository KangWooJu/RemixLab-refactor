package org.woojukang.remixlab.domain.photo.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.photo.dto.request.SelectPhotosRequest;
import org.woojukang.remixlab.domain.photo.dto.response.SelectPhotosResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.photo.service.PhotoService;
import org.woojukang.remixlab.domain.photo.entity.PhotoSelection;
import org.woojukang.remixlab.domain.quest.facade.QuestFacade;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.query.creation.dto.response.PhotoResponse;
import org.woojukang.remixlab.query.creation.dto.request.ShowPhotoRequest;
import org.woojukang.remixlab.query.creation.dto.request.ShowPhotoSelectedRequest;
import org.woojukang.remixlab.query.creation.service.CreationQueryService;
import org.woojukang.remixlab.query.creation.service.PhotoQueryService;
import org.woojukang.remixlab.query.user.service.UserQueryService;

import java.util.List;


@Component
@RequiredArgsConstructor
public class PhotoFacade {

    private final PhotoQueryService photoQueryService;
    private final PhotoService photoService;
    private final UserQueryService userQueryService;
    private final CreationQueryService creationQueryService;
    private final QuestFacade questFacade;


    // 선택한 사진만 조회하기 기능
    public PhotoResponse showPhotoSelected
    (ShowPhotoSelectedRequest showPhotoSelectedRequest,String username){

        User user = userQueryService.findByUsername(username);

        return photoQueryService
                .showPhotosOnlSelected(showPhotoSelectedRequest,user.getId());

    }

    public PhotoResponse showPhotoAll
            (ShowPhotoRequest showPhotoRequest){

        return photoQueryService
                .showPhotos(showPhotoRequest);

    }

    public SelectPhotosResponse selectPhotos
            (SelectPhotosRequest selectPhotosRequest,
             String username){

        User user = userQueryService.findByUsername(username);
        Creation creation = creationQueryService.findCreationEntityById(selectPhotosRequest.creationId());


        // 선택한 사진에 대해서 중복검사 후 , Selection 저장하기
        List<PhotoSelection> savedSelections = photoService
                .saveSelections(user,
                        creation,
                        selectPhotosRequest);

        // Response 매핑
        List<SelectPhotosResponse.Selection> responseSelections = savedSelections.stream()
                .map(s -> new SelectPhotosResponse
                        .Selection(s.getSceneNumber()))
                .toList();

        // 퀘스트 체크
        questFacade.onPhotoCreated(username);

        return new SelectPhotosResponse(creation.getId(),responseSelections);

    }


}
