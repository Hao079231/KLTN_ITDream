package com.base.auth.dto.studentTaskProgress;

import com.base.auth.dto.simulationEnrollment.SimulationEnrollmentDisplayDto;
import com.base.auth.dto.task.TaskDisplayDto;
import lombok.Data;

@Data
public class StudentTaskProgressDisplayDto {
  private Long id;
  private Integer status;
  private Integer errorCount;
  private SimulationEnrollmentDisplayDto simulationEnrollment;
  private TaskDisplayDto task;
}
