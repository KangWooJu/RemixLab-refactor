package org.woojukang.remixlab.domain.photo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.woojukang.remixlab.domain.creation.entity.Creation;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Creation creation;

    private String title;
    private String url;
    private Integer sceneNumber;

}
