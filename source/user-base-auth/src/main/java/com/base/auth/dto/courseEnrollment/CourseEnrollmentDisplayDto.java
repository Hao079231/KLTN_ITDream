package com.base.auth.dto.courseEnrollment;

import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class CourseEnrollmentDisplayDto {
  private Long id;
  private Integer status;
  private Float progress;
  private ProfileStudentDto student;
  private CourseDisplayDto course;
}
