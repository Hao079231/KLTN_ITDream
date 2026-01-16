package com.base.auth.dto.lessonProgress;

import com.base.auth.dto.student.StudentDto;
import com.base.auth.dto.lesson.LessonDto;
import lombok.Data;

@Data
public class LessonProgressDto {
  private Long id;
  private StudentDto student;
  private LessonDto task;
  private Integer currentAttempt;
  private Integer errorCount;
  private Integer state;
}
