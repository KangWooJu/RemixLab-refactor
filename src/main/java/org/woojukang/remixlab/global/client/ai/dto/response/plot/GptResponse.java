package org.woojukang.remixlab.global.client.ai.dto.response.plot;

import java.util.List;

public record GptResponse (
        String id,
        String object,
        long created,
        String model,
        List<Output> output) {

    public record Output(
            String id,
            String type,
            String role,
            List<Content> content) {

    }

    public record Content(
            String type,
            String text) {

    }
}
