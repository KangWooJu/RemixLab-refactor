package org.woojukang.remixlab.global.client.ai.dto.response.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SoraStatusResponse
        (String id,
         String object, // "video"
         String status,
         Integer progress,

         @JsonProperty("created_at")
         Long createdAt,

         @JsonProperty("updated_at")
         Long updatedAt,

         @JsonProperty("video_url")
         String videoUrl,

         ErrorResponse error) {

    public boolean isCompleted() {
        return "completed"
                .equalsIgnoreCase(status);
    }

    public boolean isProcessing() {
        return "processing"
                .equalsIgnoreCase(status)
                || "queued"
                .equalsIgnoreCase(status);
    }

    public boolean isFailed() {
        return "failed"
                .equalsIgnoreCase(status);
    }

    public record ErrorResponse(
            String message,
            String code
    ) {

    }
}