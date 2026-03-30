package org.woojukang.remixlab.global.config.exception.domain;

import org.woojukang.remixlab.global.config.exception.BaseExceptionEnum;
import org.woojukang.remixlab.global.enums.MessageCommInterface;

import java.io.Serial;

public class BadRequestException extends BaseException{

    @Serial
    private static final long serialVersionUID = -5148452197821358350L;

    public BadRequestException() {
        super(BaseExceptionEnum.BAD_REQUEST);
    }

    public BadRequestException(MessageCommInterface messageCommInterface) {
        super(messageCommInterface);
    }
}
