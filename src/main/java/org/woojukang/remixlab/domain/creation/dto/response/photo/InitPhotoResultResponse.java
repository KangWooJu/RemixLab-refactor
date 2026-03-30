package org.woojukang.remixlab.domain.creation.dto.response.photo;

import java.util.List;

public record InitPhotoResultResponse
        (List<PhotoDetail> photos) {

    public record PhotoDetail
            (Integer sceneNumber,
             String title,
             String url) {

    }
}