package org.woojukang.remixlab.domain.creation.dto.response.pipeline;

import java.util.List;

public record InitPhotoResponse(
        List<Detail> imagePrompts
) {

    public record Detail
            (Integer sceneNumber,
             String prompt){

    }

}