package org.woojukang.remixlab.domain.quest.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QuestType {

    FIRST_PLOT("첫 스토리 생성", 50),
    CREATE_5_PHOTOS("이미지 5개 생성", 100),
    FIRST_VIDEO("첫 영상 생성", 70),
    COMPLETE_3_PROJECTS("프로젝트 3개 완성", 200),
    ALL_QUESTS_COMPLETE("모든 퀘스트 완료", 500);

    private final String title;
    private final Integer rewardExp;
}
