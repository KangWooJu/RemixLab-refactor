package org.woojukang.remixlab.domain.video.facade;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.creation.dto.request.direct.DirectVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.request.pipeline.InitVideoRequest;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitVideoResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.creation.service.CreationService;
import org.woojukang.remixlab.domain.quest.facade.QuestFacade;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.video.entity.Video;
import org.woojukang.remixlab.domain.video.service.VideoService;
import org.woojukang.remixlab.global.client.ai.dto.request.AiClientRequest;
import org.woojukang.remixlab.global.client.ai.dto.response.video.SoraResponse;
import org.woojukang.remixlab.query.creation.dto.request.ShowPlotWithDetailRequest;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;
import org.woojukang.remixlab.query.creation.service.CreationQueryService;
import org.woojukang.remixlab.query.creation.service.PlotQueryService;
import org.woojukang.remixlab.query.user.service.UserQueryService;

@Component
@RequiredArgsConstructor
public class VideoPersistenceFacade {

    private final CreationQueryService creationQueryService;
    private final PlotQueryService plotQueryService;
    private final UserQueryService userQueryService;
    private final CreationService creationService;
    private final VideoService videoService;
    private final QuestFacade questFacade;

    @Transactional
    public InitVideoResponse saveVideoByPhotosBlocking(
            InitVideoRequest initVideoRequest,
            String username,
            SoraResponse soraResponse
    ) {
        Creation creation = creationQueryService
                .findCreationEntityById(initVideoRequest.creationId());

        Video video = videoService.saveVideo(creation, soraResponse);

        questFacade.onVideoCreated(username);

        return new InitVideoResponse(
                video.getId(),
                soraResponse.id(),
                "비디오 생성이 접수되었습니다."
        );
    }

    @Transactional
    public InitVideoResponse saveVideoByTextBlocking(
            String username,
            DirectVideoRequest directVideoRequest,
            AiClientRequest aiClientRequest,
            SoraResponse soraResponse
    ) {
        User user = userQueryService.findByUsername(username);

        Creation creation = creationService.makeCreationDirect(user, aiClientRequest);

        Video video = videoService.saveVideo(creation, soraResponse);

        questFacade.onVideoCreated(username);

        return new InitVideoResponse(
                video.getId(),
                soraResponse.id(),
                "비디오 생성이 접수되었습니다."
        );
    }


    public ShowPlotWithDetailResponse findPlotDetailsBlocking(Long creationId) {
        return plotQueryService.findPlotWithDetailsByCreationId(
                new ShowPlotWithDetailRequest(creationId)
        );
    }
}
