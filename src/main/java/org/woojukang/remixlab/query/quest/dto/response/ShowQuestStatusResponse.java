package org.woojukang.remixlab.query.quest.dto.response;

import java.util.List;

public record ShowQuestStatusResponse(List<Completed> quests) {

    public record Completed(String name){

    }
}
