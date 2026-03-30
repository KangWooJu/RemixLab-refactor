package org.woojukang.remixlab.global.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.global.security.entity.Refresh;

import java.util.Optional;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh,Long> {

    Optional<Refresh> deleteByRefresh(String refresh);
    Boolean existsByRefresh(String refresh);
}
