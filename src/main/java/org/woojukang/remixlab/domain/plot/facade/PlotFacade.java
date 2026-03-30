package org.woojukang.remixlab.domain.plot.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.woojukang.remixlab.query.creation.dto.response.ShowPlotResponse;
import org.woojukang.remixlab.query.creation.service.PlotQueryService;

@Component
@RequiredArgsConstructor
public class PlotFacade {

    private final PlotQueryService plotQueryService;


    // creation에 딸린 플롯 조회하기
    public ShowPlotResponse showPlot(Long creationId){

        return plotQueryService
                .findByCreation(creationId);
    }



}
