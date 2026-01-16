package com.base.auth.dto.feedback;

import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class FeedbackClientDto {
  private Long id;
  private Integer star;
  private String comment;
  private ProfileStudentDto student;
  private CourseDisplayDto simulation;
}
