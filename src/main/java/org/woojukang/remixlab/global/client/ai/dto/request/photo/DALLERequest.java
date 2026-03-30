package org.woojukang.remixlab.global.client.ai.dto.request.photo;

public record DALLERequest(String model,
                           String prompt,
                           String size) {
}
