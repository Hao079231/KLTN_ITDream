package com.base.auth.dto.lesson;

import com.base.auth.dto.chapter.ChapterDisplayDto;
import lombok.Data;

@Data
public class LessonStudentDto {
  private Long id;
  private String title;
  private String introduction;
  private String description;
  private String content;
  private String imagePath;
  private String filePath;
  private String videoPath;
  private Integer totalQuestion;
  private Integer totalError;
  private ChapterDisplayDto chapter;
}
