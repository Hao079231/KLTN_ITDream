package com.base.auth.dto.lessonProgress;

import com.base.auth.dto.courseEnrollment.CourseEnrollmentDisplayDto;
import com.base.auth.dto.lesson.LessonDisplayDto;
import lombok.Data;

@Data
public class LessonProgressDisplayDto {
  private Long id;
  private Integer status;
  private Integer errorCount;
  private CourseEnrollmentDisplayDto courseEnrollment;
  private LessonDisplayDto lesson;
}
