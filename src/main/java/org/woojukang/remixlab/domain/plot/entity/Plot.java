package org.woojukang.remixlab.domain.plot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.woojukang.remixlab.domain.creation.entity.Creation;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Plot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Creation creation;

    private String title;
    private String genre;
    private String mood;
    private String worldSetting;

    @Embedded
    private MainCharacter mainCharacter;

    @ElementCollection
    @CollectionTable(name = "plot_scene")
    private List<Scene> scenes;

}
