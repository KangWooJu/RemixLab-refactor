package org.woojukang.remixlab.global.security.dto.response;

public record FilterErrorResponse(boolean error,
                                  String method,
                                  String message,
                                  String time) {
}
