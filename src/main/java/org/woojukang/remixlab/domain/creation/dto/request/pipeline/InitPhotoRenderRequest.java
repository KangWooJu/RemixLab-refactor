package org.woojukang.remixlab.domain.creation.dto.request.pipeline;

import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPhotoResponse;

import java.util.List;

public record InitPhotoRenderRequest
        (List<Detail> imagePrompts) {

    public static InitPhotoRenderRequest from(InitPhotoResponse response){

        List<Detail> details = response
                .imagePrompts()
                .stream()
                .map(detail -> new Detail(
                        detail.sceneNumber(),
                        detail.prompt()
                ))
                .toList();

        return new InitPhotoRenderRequest(details);
    }

    public record Detail
            (Integer sceneNumber,
             String prompt){

    }
}

