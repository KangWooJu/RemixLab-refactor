package org.woojukang.remixlab.global.client.ai.dto.request.video;


public record SoraRequest(String model,
                          String prompt,
                          String seconds,
                          String size) {

}
