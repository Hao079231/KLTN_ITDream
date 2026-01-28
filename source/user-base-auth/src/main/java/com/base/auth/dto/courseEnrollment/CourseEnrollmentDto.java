package com.base.auth.dto.courseEnrollment;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.course.CourseDto;
import com.base.auth.dto.student.StudentDto;
import lombok.Data;

@Data
public class CourseEnrollmentDto extends ABasicAdminDto {
  private Float progress;
  private StudentDto student;
  private CourseDto course;
}
