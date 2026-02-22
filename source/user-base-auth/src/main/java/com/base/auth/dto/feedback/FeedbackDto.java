package com.base.auth.dto.feedback;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.course.CourseDto;
import com.base.auth.dto.student.StudentDto;
import lombok.Data;

@Data
public class FeedbackDto extends ABasicAdminDto {
  private Integer star;
  private String content;
  private StudentDto student;
  private CourseDto course;
}
