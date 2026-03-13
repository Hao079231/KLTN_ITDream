package com.base.auth.dto.task;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import lombok.Data;

@Data
public class TaskStudentDto {
  private Long id;
  private String name;
  private String title;
  private String introduction;
  private String description;
  private String content;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalQuestion;
  private Integer totalError;
  private SimulationDisplayDto simulation;
}
