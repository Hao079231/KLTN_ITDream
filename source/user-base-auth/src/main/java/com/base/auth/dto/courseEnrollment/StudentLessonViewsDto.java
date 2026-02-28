package com.base.auth.dto.courseEnrollment;

import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class StudentLessonViewsDto {
  private ProfileStudentDto student;
  private Boolean isReviewed;
}
