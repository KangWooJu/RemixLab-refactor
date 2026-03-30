package org.woojukang.remixlab.global.client.ai.dto.response.video;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SoraResponse(String id,
                           String object,
                           String status,

                           @JsonProperty("created_at")
                           Long createdAt) {
}
