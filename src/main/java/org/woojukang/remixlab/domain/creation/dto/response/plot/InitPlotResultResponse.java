package org.woojukang.remixlab.domain.creation.dto.response.plot;

import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPlotResponse;

import java.util.List;

public record InitPlotResultResponse(
        Long creationId,
        String title,
        String genre,
        String mood,
        MainCharacter mainCharacter,
        String worldSetting,
        List<Scene> scenes
) {

    public static InitPlotResultResponse from(Long creationId, InitPlotResponse source) {
        return new InitPlotResultResponse(
                creationId,
                source.title(),
                source.genre(),
                source.mood(),
                MainCharacter.from(source.mainCharacter()),
                source.worldSetting(),
                source.scenes().stream()
                        .map(Scene::from)
                        .toList()
        );
    }

    public record Scene(
            Integer sceneNumber,
            String sceneDescription,
            String visualElements,
            String cameraAngle,
            String lighting,
            String emotion,
            String motion
    ) {
        public static Scene from(InitPlotResponse.Scene source) {
            return new Scene(
                    source.sceneNumber(),
                    source.sceneDescription(),
                    source.visualElements(),
                    source.cameraAngle(),
                    source.lighting(),
                    source.emotion(),
                    source.motion()
            );
        }
    }


    public record MainCharacter(
            String name,
            String appearance,
            String personality
    ) {
        public static MainCharacter from(InitPlotResponse.MainCharacter source) {
            return new MainCharacter(
                    source.name(),
                    source.appearance(),
                    source.personality()
            );
        }
    }
}