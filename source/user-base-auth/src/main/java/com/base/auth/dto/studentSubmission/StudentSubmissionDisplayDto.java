package com.base.auth.dto.studentSubmission;

import com.base.auth.dto.taskQuestion.TaskQuestionStudentDto;
import lombok.Data;

@Data
public class StudentSubmissionDisplayDto {
  private Long id;
  private String answer;
  private TaskQuestionStudentDto taskQuestion;
}
