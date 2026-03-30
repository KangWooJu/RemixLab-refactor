package org.woojukang.remixlab.domain.creation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.creation.entity.Creation;

@Repository
public interface CreationRepository extends JpaRepository<Creation,Long> {
}
