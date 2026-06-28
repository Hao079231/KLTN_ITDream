package com.base.auth.dto.task;

import lombok.Data;

@Data
public class TaskDto {
  private Long id;
  private String title;
  private String description;
  private String content;
  private Integer kind;
  private Integer submissionType;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalError;
  private Integer totalQuestion;
  /**
   * Submission requirement for subtask (kind = 2).
   * 0 = none, 1 = file only, 2 = text only, 3 = file + text.
   */
  private Integer submissionType;
  private TaskDto parent;
}
