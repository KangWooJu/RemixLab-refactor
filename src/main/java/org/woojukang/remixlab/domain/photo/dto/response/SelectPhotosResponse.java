package org.woojukang.remixlab.domain.photo.dto.response;

import java.util.List;

public record SelectPhotosResponse(Long creationId,
                                   List<Selection> selection) {

    public record Selection
            (Integer sceneNumber){

    }
}
