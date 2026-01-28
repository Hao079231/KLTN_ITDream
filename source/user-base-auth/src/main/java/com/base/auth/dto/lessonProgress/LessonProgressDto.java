package com.base.auth.dto.lessonProgress;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.courseEnrollment.CourseEnrollmentDto;
import com.base.auth.dto.lesson.LessonDto;
import lombok.Data;

@Data
public class LessonProgressDto extends ABasicAdminDto {
  private Integer errorCount;
  private LessonDto lesson;
  private CourseEnrollmentDto courseEnrollment;
}
