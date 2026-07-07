package com.base.auth.dto.feedback;

import com.base.auth.dto.simulation.SimulationDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FeedbackClientDto {
  private Long id;
  private Integer star;
  private String content;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private ProfileStudentDto student;
  @JsonIgnoreProperties({"educator", "category"})
  private SimulationDisplayDto simulation;
}
