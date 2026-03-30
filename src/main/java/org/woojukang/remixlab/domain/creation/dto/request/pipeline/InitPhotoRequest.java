package org.woojukang.remixlab.domain.creation.dto.request.pipeline;

import java.util.List;

public record InitPhotoRequest
        (String title,
         String genre,
         String mood,
         MainCharacter mainCharacter,
         String worldSetting,
         List<Scene> scenes) {

    public record Scene(
            Integer sceneNumber,
            String sceneDescription,
            String visualElements,
            String cameraAngle,
            String lighting,
            String emotion,
            String motion) {

    }

    public record MainCharacter(
            String name,
            String appearance,
            String personality) {

    }
}
