package com.base.auth.dto.task;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import lombok.Data;

@Data
public class TaskDisplayDto {
  private Long id;
  private String name;
  private String title;
  private String introduction;
  private String description;
  private SimulationDisplayDto simulation;
}
