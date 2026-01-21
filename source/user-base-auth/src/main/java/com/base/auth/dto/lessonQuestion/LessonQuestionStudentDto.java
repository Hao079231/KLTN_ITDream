package com.base.auth.dto.lessonQuestion;

import com.base.auth.dto.lesson.LessonStudentDto;
import lombok.Data;

@Data
public class LessonQuestionStudentDto {
  private Long id;
  private String question;
  private String questionType;
  private String options;
  private LessonStudentDto lesson;
}
