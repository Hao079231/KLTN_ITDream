package com.base.auth.dto.taskQuestion;

import com.base.auth.dto.task.TaskEducatorDto;
import lombok.Data;

@Data
public class TaskQuestionEducatorDto {
  private Long id;
  private String question;
  private String options;
  private TaskEducatorDto task;
}
