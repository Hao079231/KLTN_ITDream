package com.base.auth.dto.studentTaskProgress;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.simulationEnrollment.SimulationEnrollmentDto;
import com.base.auth.dto.task.TaskDto;
import lombok.Data;

@Data
public class StudentTaskProgressDto extends ABasicAdminDto {
  private Integer errorCount;
  private TaskDto task;
  private SimulationEnrollmentDto simulationEnrollment;
}
