package org.woojukang.remixlab.domain.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.photo.entity.PhotoSelection;
import org.woojukang.remixlab.domain.user.entity.User;

@Repository
public interface PhotoSelectionRepository extends JpaRepository<PhotoSelection,Long> {

    boolean existsByUserIdAndCreationIdAndSceneNumber(Long userId,
                                                      Long creationId,
                                                      Integer sceneNumber);

}
