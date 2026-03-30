package org.woojukang.remixlab.domain.video.dto.response;

import org.woojukang.remixlab.domain.video.entity.VideoStatus;

public record VideoStatusResponse(Long videoId,
                                  String status,
                                  String url) {
}
