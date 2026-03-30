package org.woojukang.remixlab.query.creation.dto.response;

import java.util.List;

public record ShowPlotWithDetailResponse (
        Long plotId,
        String title,
        String genre,
        String mood,
        String worldSetting,
        String appearance,
        String name,
        String personality,
        List<SceneResponse> scenes
) {

    public record SceneResponse(
            Integer sceneNumber,
            String sceneDescription,
            String cameraAngle,
            String emotion,
            String lighting,
            String motion,
            String visualElements
    ) {
    }
}