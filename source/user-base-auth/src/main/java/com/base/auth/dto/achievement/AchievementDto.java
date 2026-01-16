package com.base.auth.dto.achievement;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.course.CourseDto;
import com.base.auth.dto.student.StudentDto;
import lombok.Data;

@Data
public class AchievementDto extends ABasicAdminDto {
  private String filePath;
  private CourseDto simulation;
  private StudentDto student;
}
