package com.base.auth.dto.achievement;

import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class AchievementStudentDto {
  private Long id;
  private String filePath;
  private ProfileStudentDto student;
  private CourseDisplayDto course;
}
