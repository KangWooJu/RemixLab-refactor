package org.woojukang.remixlab.query.creation.dto.response;

import java.util.List;

public record ShowMyCreationResponse(List<CreationDetails> creations) {

    public record CreationDetails
            (Long creationId,
             String title){

    }

}
