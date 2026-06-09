package com.base.auth.dto.task;

import lombok.Data;

@Data
public class TaskStudentDto {
  private Long id;
  private String name;
  private String title;
  private String description;
  private String content;
  private Integer kind;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalQuestion;
  private Integer totalError;
}
