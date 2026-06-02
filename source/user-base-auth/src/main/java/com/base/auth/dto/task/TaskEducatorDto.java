package com.base.auth.dto.task;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import lombok.Data;

@Data
public class TaskEducatorDto {
  private Long id;
  private String name;
  private String title;
  private String description;
  private String content;
  private Integer kind;
  private Integer type;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalError;
  private Integer totalQuestion;
  private SimulationDisplayDto simulation;
}
