package com.base.auth.dto.achievement;

import com.base.auth.dto.course.CourseDisplayDto;
import lombok.Data;

@Data
public class AchievementStudentDto {
  private Long id;
  private String filePath;
  private String studentName;
  private CourseDisplayDto simulation;
}
