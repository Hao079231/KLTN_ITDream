package com.base.auth.dto.simulationEnrollment;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class SimulationEnrollmentDisplayDto {
  private Long id;
  private Integer status;
  private Float progress;
  private ProfileStudentDto student;
  private SimulationDisplayDto simulation;
}
