package org.woojukang.remixlab.domain.plot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woojukang.remixlab.domain.plot.entity.Plot;
import org.woojukang.remixlab.domain.user.entity.User;

@Repository
public interface PlotRepository extends JpaRepository<Plot,Integer> {

}
