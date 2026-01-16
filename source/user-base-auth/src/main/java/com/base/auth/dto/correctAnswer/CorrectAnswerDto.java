package com.base.auth.dto.correctAnswer;

import com.base.auth.dto.lessonProgress.LessonProgressDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionDto;
import lombok.Data;

@Data
public class CorrectAnswerDto {
  private Long id;
  private LessonProgressDto studentSubTaskProgress;
  private LessonQuestionDto taskQuestion;
  private String answer;
  private Boolean isCorrect;
}
