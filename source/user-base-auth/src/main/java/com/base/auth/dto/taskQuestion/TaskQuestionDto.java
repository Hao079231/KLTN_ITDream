package com.base.auth.dto.taskQuestion;

import com.base.auth.dto.task.TaskDto;
import lombok.Data;

@Data
public class TaskQuestionDto {
  private Long id;
  private String question;
  private String options;
  private TaskDto task;
}
