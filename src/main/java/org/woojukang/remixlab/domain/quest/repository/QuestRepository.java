package org.woojukang.remixlab.domain.quest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.quest.entity.Quest;
import org.woojukang.remixlab.domain.quest.entity.QuestType;
import org.woojukang.remixlab.domain.user.entity.User;

@Repository
public interface QuestRepository extends JpaRepository<Quest,Long> {

    boolean existsByUserAndQuestType(User user, QuestType questType);
}
