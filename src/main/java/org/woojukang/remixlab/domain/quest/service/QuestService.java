package org.woojukang.remixlab.domain.quest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.woojukang.remixlab.domain.quest.entity.Quest;
import org.woojukang.remixlab.domain.quest.entity.QuestType;
import org.woojukang.remixlab.domain.quest.repository.QuestRepository;
import org.woojukang.remixlab.domain.user.entity.User;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;

    public boolean existsByUserAndQuestType
            (User user,
             QuestType questType){

        return questRepository.existsByUserAndQuestType(user,questType);
    }

    public Quest makeQuest(User user,QuestType questType){

        return Quest
                .builder()
                .questType(questType)
                .user(user)
                .completed(true)
                .build();
    }

    public void saveQuest(Quest quest){
        questRepository.save(quest);
    }
}
