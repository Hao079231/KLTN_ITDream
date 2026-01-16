package com.base.auth.dto.correctAnswer;

import com.base.auth.dto.lessonProgress.LessonProgressDisplayDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionStudentDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class CorrectAnswerDisplayDto {
  private Long id;
  private LessonProgressDisplayDto studentSubTaskProgress;
  @JsonIgnoreProperties({"task"})
  private LessonQuestionStudentDto taskQuestion;
  private String answer;
  private Boolean isCorrect;
}
