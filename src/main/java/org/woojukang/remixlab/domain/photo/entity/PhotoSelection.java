package org.woojukang.remixlab.domain.photo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.woojukang.remixlab.domain.creation.entity.Creation;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "creation_id", "scene_number"}
                )
        },indexes = {
        @Index(name = "idx_user_creation", columnList = "user_id, creation_id")
}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class PhotoSelection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creation_id")
    private Creation creation;

    @JoinColumn(name = "creation_id")
    private Integer sceneNumber;

}
