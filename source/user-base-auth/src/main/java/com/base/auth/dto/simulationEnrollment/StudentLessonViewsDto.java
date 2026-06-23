package com.base.auth.dto.simulationEnrollment;

import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class StudentLessonViewsDto {
  private Long id;
  private ProfileStudentDto student;
  private Boolean isReviewed;
  private Integer reviewStatus;
}
