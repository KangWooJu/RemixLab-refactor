package org.woojukang.remixlab.query.creation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.plot.entity.Plot;
import org.woojukang.remixlab.domain.user.entity.User;
import org.woojukang.remixlab.query.creation.dto.request.ShowPlotWithDetailRequest;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotResponse;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotWithDetailResponse;
import org.woojukang.remixlab.query.creation.repository.PlotQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlotQueryService {

    private final PlotQueryRepository plotQueryRepository;

    public Creation findCreationByPlot(Long plotId){

        return plotQueryRepository
                .findCreationByPlot(plotId);
    }

    public Plot findByTitle(String title){

        return plotQueryRepository
                .findByTitle(title);

    }

    public ShowPlotResponse findByCreation(Long creationId){

        return plotQueryRepository
                .findByCreationId(creationId);
    }

    public ShowPlotWithDetailResponse findPlotWithDetailsByCreationId
            (ShowPlotWithDetailRequest showPlotWithDetailRequest){

        return plotQueryRepository
                .findPlotWithScenes(showPlotWithDetailRequest.creationId());
    }

    public Integer countPlotByUser(User user){
        return plotQueryRepository.countPlotByUser(user);
    }



}
