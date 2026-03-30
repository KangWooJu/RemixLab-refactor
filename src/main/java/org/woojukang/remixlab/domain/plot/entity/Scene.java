package org.woojukang.remixlab.domain.plot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Scene {

    private Integer sceneNumber;

    @Column(columnDefinition = "TEXT")
    private String sceneDescription;

    @Column(columnDefinition = "TEXT")
    private String visualElements;

    private String cameraAngle;
    private String lighting;
    private String emotion;
    private String motion;


}
