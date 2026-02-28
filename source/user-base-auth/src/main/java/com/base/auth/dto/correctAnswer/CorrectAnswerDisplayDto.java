package com.base.auth.dto.correctAnswer;

import com.base.auth.dto.lessonQuestion.LessonQuestionStudentDto;
import lombok.Data;

@Data
public class CorrectAnswerDisplayDto {
  private Long id;
  private String answer;
  private LessonQuestionStudentDto lessonQuestion;
}
