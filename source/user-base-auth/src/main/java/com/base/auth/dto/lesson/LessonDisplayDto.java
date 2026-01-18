package com.base.auth.dto.lesson;

import com.base.auth.dto.chapter.ChapterDisplayDto;
import lombok.Data;

@Data
public class LessonDisplayDto {
  private Long id;
  private String title;
  private String introduction;
  private String description;
  private ChapterDisplayDto chapter;
}
