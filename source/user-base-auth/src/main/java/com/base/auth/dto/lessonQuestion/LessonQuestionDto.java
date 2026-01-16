package com.base.auth.dto.lessonQuestion;

import com.base.auth.dto.lesson.LessonDto;
import lombok.Data;

@Data
public class LessonQuestionDto {
  private Long id;
  private String question;
  private Integer questionType;
  private String options;
  private LessonDto task;
}
