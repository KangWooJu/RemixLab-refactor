package org.woojukang.remixlab.domain.creation.dto.response.pipeline;

import java.util.List;

public record InitPhotoRenderResponse(List<Images> images) {

    public record Images(String sceneNumber,
                         String imageBase64){

    }
}
