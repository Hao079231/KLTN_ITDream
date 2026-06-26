package com.base.auth.dto.task;

import lombok.Data;

@Data
public class TaskEducatorDto {
  private Long id;
  private String title;
  private String description;
  private String content;
  private Integer kind;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalError;
  private Integer totalQuestion;
}
