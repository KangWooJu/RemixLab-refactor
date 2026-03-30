package org.woojukang.remixlab.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.woojukang.remixlab.global.jpa.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String password;

    private Role role;

    private Integer exp;

    public void addExp(Integer amount){
        this.exp += amount;
    }


}
