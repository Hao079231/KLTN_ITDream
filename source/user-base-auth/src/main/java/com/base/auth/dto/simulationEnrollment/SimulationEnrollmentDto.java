package com.base.auth.dto.simulationEnrollment;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.simulation.SimulationDto;
import com.base.auth.dto.student.StudentDto;
import lombok.Data;

@Data
public class SimulationEnrollmentDto extends ABasicAdminDto {
  private Float progress;
  private StudentDto student;
  private SimulationDto simulation;
}
