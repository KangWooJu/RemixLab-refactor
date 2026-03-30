package org.woojukang.remixlab.domain.plot.entity;

import jakarta.persistence.Embeddable;
import lombok.*;


@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class MainCharacter {

    private String name;
    private String appearance;
    private String personality;

}
