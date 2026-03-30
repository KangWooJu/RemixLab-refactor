package org.woojukang.remixlab.query.quest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.woojukang.remixlab.query.quest.dto.response.ShowQuestStatusResponse;
import org.woojukang.remixlab.query.quest.repository.QuestQueryRepository;

@Service
@RequiredArgsConstructor
public class QuestQueryService {

    private final QuestQueryRepository questQueryRepository;

    public ShowQuestStatusResponse showQuestStatusResponse(Long userId){

        return questQueryRepository
                .showCompletedQuestStatus(userId);

    }
}
