package com.base.auth.dto.lesson;

import com.base.auth.dto.chapter.ChapterDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class LessonDto {
  private Long id;
  private String title;
  private String introduction;
  private String description;
  private String content;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalError;
  private Integer totalQuestion;
  private ChapterDto chapter;
}
