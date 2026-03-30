package org.woojukang.remixlab.domain.photo.dto.request;

import java.util.List;

public record SelectPhotosRequest(Long creationId,
                                  List<Selection> selections) {

    public record Selection(Integer sceneNumber){

    }
}
