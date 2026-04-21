package org.woojukang.remixlab.domain.creation.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitPhotoRequest;
import org.woojukang.remixlab.domain.creation.dto.response.direct.DirectPhotoResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.DirectPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.photo.InitPhotoResultResponse;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoRenderResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.creation.service.CreationService;
import org.woojukang.remixlab.domain.photo.entity.Photo;
import org.woojukang.remixlab.domain.photo.service.PhotoService;
import org.woojukang.remixlab.domain.quest.facade.QuestFacade;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.global.client.ai.dto.request.AiClientRequest;
import org.woojukang.remixlab.query.creation.service.PlotQueryService;
import org.woojukang.remixlab.query.user.service.UserQueryService;

import java.util.List;

/*
JPA Blocking 메소드를 따로 보관하는 파사드
 */
@Component
@RequiredArgsConstructor
public class CreationPersistenceFacade {

    private final PlotQueryService plotQueryService;
    private final UserQueryService userQueryService;

    private final CreationService creationService;
    private final PhotoService photoService;

    private final QuestFacade questFacade;



    @Transactional
    public InitPhotoResultResponse saveInitPhotosBlocking(
            InitPhotoRenderResponse initPhotoRenderResponse,
            InitPhotoRequest initPhotoRequest) {

        Creation creation = plotQueryService
                .findCreationByPlot(
                        plotQueryService
                                .findByTitle(initPhotoRequest.title())
                                .getId()
                );

        List<Photo> photoList = photoService.savePhotos(
                creation,
                initPhotoRenderResponse,
                initPhotoRequest
        );

        return photoService.makeResult(photoList);
    }

    @Transactional
    protected DirectPhotoResultResponse saveDirectPhotoBlocking(
            DirectPhotoResponse directPhotoResponse,
            String username,
            AiClientRequest aiClientRequest) {

        User user = userQueryService.findByUsername(username);

        Creation creation = creationService.makeCreationDirect(user, aiClientRequest);

        DirectPhotoResultResponse result =
                photoService.saveDirectPhoto(creation, directPhotoResponse);

        questFacade.onPhotoCreated(username);

        return result;
    }
}
