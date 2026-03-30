package org.woojukang.remixlab.domain.creation.entity;

import lombok.RequiredArgsConstructor;
import org.woojukang.remixlab.global.constant.enums.CodeCommInterface;

@RequiredArgsConstructor
public enum CreationType implements CodeCommInterface {

    INIT("I","Creation Sequentially"),
    DIRECT("D","Creation Directly");

    private final String code;
    private final String description;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCodeName() {
        return description;
    }
}
