package org.woojukang.remixlab.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.woojukang.remixlab.global.constant.enums.CodeCommInterface;

@Getter
@RequiredArgsConstructor
public enum Role implements CodeCommInterface {


    USER("U","This is user"),
    ADMIN("A","This is Admin");

    private final String code;
    private final String codeName;


}
