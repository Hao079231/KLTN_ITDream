package com.base.auth.dto.reviewSubmission;

import com.base.auth.dto.correctAnswer.CorrectAnswerDisplayDto;
import lombok.Data;

@Data
public class ReviewSubmissionDisplayDto {
  private Long id;
  private String content;
  private CorrectAnswerDisplayDto correctAnswer;
}
