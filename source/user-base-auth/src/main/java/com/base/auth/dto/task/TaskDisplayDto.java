package com.base.auth.dto.task;

import lombok.Data;

@Data
public class TaskDisplayDto {
  private Long id;
  private String title;
  private String content;
  private String description;
  private Integer kind;
  private Integer submissionType;
  private Integer orderInParent;
  private TaskDisplayDto parent;
}
