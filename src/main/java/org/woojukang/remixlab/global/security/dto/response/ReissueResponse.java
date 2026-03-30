package org.woojukang.remixlab.global.security.dto.response;

public record ReissueResponse(String status,
                              String message,
                              String AccessToken,
                              String refreshToken) {
}
