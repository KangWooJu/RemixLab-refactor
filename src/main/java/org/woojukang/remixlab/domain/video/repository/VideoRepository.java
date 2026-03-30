package org.woojukang.remixlab.domain.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.video.entity.Video;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {

    List<Video> findByVideoStatusIn(List<String> statuses);
}
