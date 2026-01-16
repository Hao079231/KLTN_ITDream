package com.base.auth.dto.lessonQuestion;

import com.base.auth.dto.lesson.LessonEducatorDto;
import lombok.Data;

@Data
public class LessonQuestionEducatorDto {
  private Long id;
  private String question;
  private Integer questionType;
  private String options;
  private LessonEducatorDto task;
}
