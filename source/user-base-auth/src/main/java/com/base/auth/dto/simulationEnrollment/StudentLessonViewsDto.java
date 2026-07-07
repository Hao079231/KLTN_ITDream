package com.base.auth.dto.simulationEnrollment;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class StudentLessonViewsDto {
  private Long id;
  @JsonIgnoreProperties({"educator"})
  private SimulationDisplayDto simulation;
  private ProfileStudentDto student;
  private Boolean isReviewed;
  private Integer reviewStatus;
}
