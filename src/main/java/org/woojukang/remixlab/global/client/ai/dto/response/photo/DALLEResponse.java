package org.woojukang.remixlab.global.client.ai.dto.response.photo;

import java.util.List;

public record DALLEResponse (
        long created,
        List<ImageData> data
) {

    public record ImageData(
            String b64_json) {

    }
}
