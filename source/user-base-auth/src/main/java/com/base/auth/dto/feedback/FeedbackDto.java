package com.base.auth.dto.feedback;

import com.base.auth.dto.course.CourseDto;
import com.base.auth.dto.student.StudentDto;
import lombok.Data;

@Data
public class FeedbackDto {
  private Long id;
  private Integer star;
  private String comment;
  private StudentDto student;
  private CourseDto simulation;
}
