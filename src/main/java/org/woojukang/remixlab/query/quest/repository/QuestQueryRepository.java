package org.woojukang.remixlab.query.quest.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.quest.entity.QQuest;
import org.woojukang.remixlab.query.quest.dto.response.ShowQuestStatusResponse;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QQuest qQuest = QQuest.quest;


    public ShowQuestStatusResponse showCompletedQuestStatus(Long userId){

        List<ShowQuestStatusResponse.Completed> result =

                jpaQueryFactory
                        .select(qQuest
                        .questType)
                .from(qQuest)
                .where(qQuest
                        .completed
                                .isTrue(),
                        qQuest
                                .user
                                .id
                                .eq(userId))
                .fetch()
                .stream()
                .map(quest-> new ShowQuestStatusResponse.Completed(quest.getTitle()))
                .toList();

        return new ShowQuestStatusResponse(result);

    }
}
