package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.model.Simulation;
import com.base.auth.model.Task;
import com.base.auth.repository.AchievementRepository;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.FeedbackRepository;
import com.base.auth.repository.TaskRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class SimulationService {
  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  TaskRepository taskRepository;

  @Autowired
  SimulationEnrollmentRepository simulationEnrollmentRepository;

  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  AchievementRepository achievementRepository;

  @Autowired
  TaskService taskService;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Transactional
  public void deleteAllBySimulation(Simulation simulation) {
    List<Task> tasks = taskRepository.findAllBySimulationId(simulation.getId());
    for (Task task : tasks){
      taskService.deleteFileInTask(task);
      taskService.deleteAllTask(task);
    }
    achievementRepository.setNullBySimulationId(simulation.getId());
    feedbackRepository.deleteAllBySimulationId(simulation.getId());
    simulationEnrollmentRepository.deleteAllBySimulationId(simulation.getId());
    simulationRepository.deleteSimulationJobBySimulationId(simulation.getId());
    simulationRepository.delete(simulation);
  }

  public void deleteFileSimulation(Simulation simulation){
    if (StringUtils.isNotBlank(simulation.getThumbnail()) && !simulation.getThumbnail().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(simulation.getThumbnail());
    }

    if (StringUtils.isNotBlank(simulation.getVideoPath()) && !simulation.getVideoPath().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(simulation.getVideoPath());
    }
  }
}
