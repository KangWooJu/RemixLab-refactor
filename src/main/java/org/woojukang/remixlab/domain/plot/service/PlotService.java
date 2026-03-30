package org.woojukang.remixlab.domain.plot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woojukang.remixlab.domain.creation.dto.response.pipeline.InitPlotResponse;
import org.woojukang.remixlab.domain.creation.entity.Creation;
import org.woojukang.remixlab.domain.plot.entity.MainCharacter;
import org.woojukang.remixlab.domain.plot.entity.Plot;
import org.woojukang.remixlab.domain.plot.entity.Scene;
import org.woojukang.remixlab.domain.plot.repository.PlotRepository;
import org.woojukang.remixlab.domain.user.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlotService {

  private final PlotRepository plotRepository;

  // plot 저장하기
  public void savePlot(Creation creation,InitPlotResponse initPlotResponse){

      Plot plot = Plot
              .builder()
              .creation(creation)
              .title(initPlotResponse.title())
              .genre(initPlotResponse.genre())
              .mood(initPlotResponse.mood())
              .mainCharacter(parsingMainCharacter(initPlotResponse))
              .scenes(parsingScenes(initPlotResponse))
              .build();

      plotRepository.save(plot);

  }



  /*
   Util 메소드 모음
   */
  public List<Scene> parsingScenes
          (InitPlotResponse initPlotResponse){

      return initPlotResponse.scenes()
              .stream()
              .map(scene -> new Scene(
                      scene.sceneNumber(),
                      scene.sceneDescription(),
                      scene.visualElements(),
                      scene.cameraAngle(),
                      scene.lighting(),
                      scene.emotion(),
                      scene.motion()
              ))
              .toList();

  }

  public MainCharacter parsingMainCharacter
          (InitPlotResponse initPlotResponse){

      return new MainCharacter(
              initPlotResponse.mainCharacter().name(),
              initPlotResponse.mainCharacter().appearance(),
              initPlotResponse.mainCharacter().personality()
      );

  }



}
