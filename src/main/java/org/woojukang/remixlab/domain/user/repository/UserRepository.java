package org.woojukang.remixlab.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.woojukang.remixlab.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);



}
