package org.woojukang.remixlab.global.constant.enums;

import org.woojukang.remixlab.global.constant.serializer.CodeCommDeserializer;
import org.woojukang.remixlab.global.constant.serializer.CodeCommSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(
        using = CodeCommSerializer.class
)
@JsonDeserialize(
        using = CodeCommDeserializer.class
)
public interface CodeCommInterface {
    String getCode();
    String getCodeName();
}
