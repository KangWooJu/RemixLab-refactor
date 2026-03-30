package org.woojukang.remixlab.global.config.exception;

import lombok.RequiredArgsConstructor;
import org.woojukang.remixlab.global.enums.MessageCommInterface;

@RequiredArgsConstructor
public enum BaseExceptionEnum implements MessageCommInterface {


    EXCEPTION_ISSUED("BASE.EXCEPTION.EXCEPTION_ISSUED", "시스템에러 발생, 관리자에게 문의하세요."),
    EXCEPTION_VALIDATION("BASE.EXCEPTION.EXCEPTION_VALIDATION", "요청 값 검증 실패"),
    ENTITY_NOT_FOUND("BASE.EXCEPTION.ENTITY_NOT_FOUND", "조회 대상이 없습니다."),
    FORBIDDEN("BASE.EXCEPTION.FORBIDDEN", "인증 실패 : {0}"),
    BAD_REQUEST("BASE.EXCEPTION.BAD_REQUEST", "잘못된 요청입니다."),
    ACCESS_DENIED("BASE.EXCEPTION.ACCESS_DENIED", "{0}"),
    USER_NOT_FOUND("BASE.EXCEPTION.USER_NOT_FOUND","해당 유저를 찾을 수 없습니다."),
    LLM_API_RESPONSE_NOT_FOUND("BASE.EXCEPTION.LLM_API_RESPONSE_NOT_FOUND","LLM API의 응답결과가 없습니다."),
    JSON_NOT_MATCHED("BASE.EXCEPTION.JSON_NOT_MATCHED","입력받은 JSON타입과 DTO의 매칭에 실패하였습니다."),
    FILE_UPLOAD_FAILED("BASE.EXCEPTION.FILE_UPLOAD_FAILED","파일 업로드에 실패하였습니다.")
    ;

    private final String errorCode;
    private final String message;

    @Override
    public String getCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}