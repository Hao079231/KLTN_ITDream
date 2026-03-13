package com.base.auth.dto.task;

import com.base.auth.dto.simulation.SimulationDto;
import lombok.Data;

@Data
public class TaskDto {
  private Long id;
  private String name;
  private String title;
  private String introduction;
  private String description;
  private String content;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalError;
  private Integer totalQuestion;
  private SimulationDto simulation;
}
