package org.woojukang.remixlab.query.creation.dto.response;

import java.util.List;

public record ShowPlotResponse(String genre,
                               String title,
                               String mood,
                               List<SceneDetail> sceneDetails,
                               String mainCharacter
                               ) {

    public record SceneDetail(Integer sceneNumber,
                              String sceneDescription){

    }

}
