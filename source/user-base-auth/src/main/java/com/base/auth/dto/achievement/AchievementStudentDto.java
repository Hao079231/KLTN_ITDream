package com.base.auth.dto.achievement;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class AchievementStudentDto {
  private Long id;
  private String filePath;
  private ProfileStudentDto student;
  private SimulationDisplayDto simulation;
}
