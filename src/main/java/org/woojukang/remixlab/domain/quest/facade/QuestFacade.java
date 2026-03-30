package org.woojukang.remixlab.domain.quest.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.domain.photo.service.PhotoService;
import org.woojukang.remixlab.domain.plot.service.PlotService;
import org.woojukang.remixlab.domain.quest.entity.QuestType;
import org.woojukang.remixlab.domain.quest.service.QuestService;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.domain.video.service.VideoService;
import org.woojukang.remixlab.query.creation.service.CreationQueryService;
import org.woojukang.remixlab.query.creation.service.PhotoQueryService;
import org.woojukang.remixlab.query.creation.service.PlotQueryService;
import org.woojukang.remixlab.query.creation.service.VideoQueryService;
import org.woojukang.remixlab.query.quest.dto.response.ShowQuestStatusResponse;
import org.woojukang.remixlab.query.quest.service.QuestQueryService;
import org.woojukang.remixlab.query.user.service.UserQueryService;

@Component
@RequiredArgsConstructor
public class QuestFacade {


    private final QuestService questService;

    private final UserQueryService userQueryService;
    private final CreationQueryService creationQueryService;
    private final PlotQueryService plotQueryService;
    private final PhotoQueryService photoQueryService;
    private final VideoQueryService videoQueryService;
    private final QuestQueryService questQueryService;


    public void onPlotCreated(String username){

        User user = userQueryService.findByUsername(username);

        Integer count = plotQueryService
                .countPlotByUser(user);


        if(count == 1){
            complete(user,QuestType.FIRST_PLOT);
        }

        checkProjectCompletion(user);
        checkAllCompleted(user);

    }

    public void onPhotoCreated(String username){

        User user = userQueryService.findByUsername(username);

        Integer count = photoQueryService
                .countSelectedPhotosByUser(user);

        System.out.println("갯수 :" + count);


        if (count == 5){
            complete(user,QuestType.CREATE_5_PHOTOS);
        }

        checkProjectCompletion(user);
        checkAllCompleted(user);

    }

    public void onVideoCreated(String username){

        User user = userQueryService
                .findByUsername(username);

        Integer count = videoQueryService
                .countVideoByUser(user);

        // QueryDSL 대체

        if(count == 1){
            complete(user,QuestType.FIRST_VIDEO);
        }

        checkProjectCompletion(user);
        checkAllCompleted(user);

    }


    /* 퀘스트 완료 갯수 메소드 ( 매 메소드마다 체크 )
     */


    // 퀘스트 : 프로젝트 3개 완수
    private void checkProjectCompletion(User user){

        Long completedCount = creationQueryService
                .countCompletedProjects(user);

        if(completedCount >= 3){
            complete(user,QuestType.COMPLETE_3_PROJECTS);
        }
    }

    // 퀘스트 : 모든 퀘스트 완료
    private void checkAllCompleted(User user){

        boolean allDone = isCompleted(user,QuestType.FIRST_PLOT) &&
                isCompleted(user,QuestType.CREATE_5_PHOTOS) &&
                isCompleted(user,QuestType.FIRST_VIDEO) &&
                isCompleted(user,QuestType.COMPLETE_3_PROJECTS);

        if(allDone){
            complete(user,QuestType.ALL_QUESTS_COMPLETE);
        }


    }



    /* 공통 처리 로직
     */

    // 퀘스트 완수
    private void complete(User user,QuestType questType){

        if(isCompleted(user, questType)) {
            return;
        }

        // Quest 객체 생성 후 , 저장하기
        questService
                .saveQuest(questService
                        .makeQuest(user,
                                questType));

        user.addExp(questType.getRewardExp());
    }


    private boolean isCompleted(User user, QuestType questType){
        return questService
                .existsByUserAndQuestType(user,
                        questType);
    }

    /* 조회 모듈
     */

    public ShowQuestStatusResponse showQuestStatus(String username){

        User user = userQueryService.findByUsername(username);

        return questQueryService
                .showQuestStatusResponse(user.getId());

    }


}
