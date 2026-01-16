package com.base.auth.dto.lesson;

import com.base.auth.model.Lesson;
import lombok.Data;

@Data
public class LessonEducatorDto {
  private Long id;
  private String name;
  private String description;
  private String title;
  private String introduction;
  private String content;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer kind;
  private Integer maxErrors;
  private Integer totalQuestion;
  private Lesson parent;
}
