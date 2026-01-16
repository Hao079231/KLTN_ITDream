package com.base.auth.dto.lesson;

import lombok.Data;

@Data
public class LessonStudentDto {
  private Long id;
  private String name;
  private String description;
  private String title;
  private String introduction;
  private String content;
  private String imagePath;
  private String filePath;
  private String videoPath;
}
