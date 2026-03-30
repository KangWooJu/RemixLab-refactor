package org.woojukang.remixlab.domain.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.photo.entity.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
}
