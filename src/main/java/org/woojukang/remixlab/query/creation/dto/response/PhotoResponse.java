package org.woojukang.remixlab.query.creation.dto.response;

import java.util.List;

public record PhotoResponse(List<photoDetails> phots) {

    public record photoDetails(Integer sceneNumber,
                               String url){

    }
}
