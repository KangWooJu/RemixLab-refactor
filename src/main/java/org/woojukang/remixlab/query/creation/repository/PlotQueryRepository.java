package org.woojukang.remixlab.query.creation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.creation.entity.QCreation;
import org.woojukang.remixlab.domain.plot.entity.Plot;

import org.woojukang.remixlab.domain.plot.entity.QPlot;
import org.woojukang.remixlab.domain.plot.entity.QScene;
import org.woojukang.remixlab.domain.plot.entity.Scene;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.config.exception.domain.BaseException;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotResponse;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlotQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPlot qPlot = QPlot.plot;
    private final QScene qScene = QScene.scene;
    private final QCreation qCreation = QCreation.creation;

    // 디자인 보고 나중에 , 필요한 컬럼만 뺴서 볼 수 있도록

    public Creation findCreationByPlot(Long plotId){

        return jpaQueryFactory
                .select(qPlot.creation)
                .from(qPlot)
                .where(qPlot.id.eq(plotId))
                .fetchOne();

    }

    public Plot findByTitle(String title){

        return jpaQueryFactory.select(qPlot)
                .from(qPlot)
                .where(qPlot.title.eq(title))
                .fetchOne();

    }

    public ShowPlotResponse findByCreationId (Long creationId){

        Plot plot = jpaQueryFactory
                .selectFrom(qPlot)
                .where(qPlot.creation.id.eq(creationId))
                .fetchOne();

        if (plot == null) {
            throw new BaseException(BaseExceptionEnum.ENTITY_NOT_FOUND);
        }

        List<ShowPlotResponse.SceneDetail> sceneDetails =
                plot.getScenes().stream()
                        .map(s -> new ShowPlotResponse.SceneDetail(
                                s.getSceneNumber(),
                                s.getSceneDescription()
                        ))
                        .toList();

        return new ShowPlotResponse(
                plot.getGenre(),
                plot.getTitle(),
                plot.getMood(),
                sceneDetails,
                plot.getMainCharacter().getName()
        );
    }

    public ShowPlotWithDetailResponse findPlotWithScenes(Long creationId) {

        List<Tuple> result = jpaQueryFactory
                .select(qPlot, qScene)
                .from(qPlot)
                .leftJoin(qPlot.scenes, qScene)
                .where(qPlot.creation.id.eq(creationId))
                .orderBy(qScene.sceneNumber.asc())
                .fetch();

        Plot plot = null;
        List<ShowPlotWithDetailResponse.SceneResponse> scenes = new ArrayList<>();

        for (Tuple t : result) {

            Plot p = t.get(qPlot);
            Scene s = t.get(qScene);

            if (plot == null) {
                plot = p;
            }

            if (s != null) {
                scenes.add(new ShowPlotWithDetailResponse.SceneResponse(
                        s.getSceneNumber(),
                        s.getSceneDescription(),
                        s.getCameraAngle(),
                        s.getEmotion(),
                        s.getLighting(),
                        s.getMotion(),
                        s.getVisualElements()
                ));
            }
        }

        if (plot == null) {
            return null;
        }

        return new ShowPlotWithDetailResponse(
                plot.getId(),
                plot.getTitle(),
                plot.getGenre(),
                plot.getMood(),
                plot.getWorldSetting(),
                plot.getMainCharacter().getAppearance(),
                plot.getMainCharacter().getName(),
                plot.getMainCharacter().getPersonality(),
                scenes
        );
    }

    public Integer countPlotByUser(User user){

        Long count =  jpaQueryFactory
                .select(qPlot.count())
                .from(qPlot)
                .join(qPlot.creation, qCreation)
                .where(qCreation.user.eq(user))
                .fetchOne();


        return count != null ? count.intValue() : 0;
    }



}
