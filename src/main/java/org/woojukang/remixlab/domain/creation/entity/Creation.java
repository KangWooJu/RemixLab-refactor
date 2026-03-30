package org.woojukang.remixlab.domain.creation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.woojukang.remixlab.domain.user.entity.User;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Creation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String userPrompt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private CreationType creationType;

}
