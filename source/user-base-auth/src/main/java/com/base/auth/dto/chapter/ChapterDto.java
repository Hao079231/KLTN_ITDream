package com.base.auth.dto.chapter;

import com.base.auth.dto.course.CourseDto;
import lombok.Data;

@Data
public class ChapterDto {
  private Long id;
  private String name;
  private Integer chapterOrder;
  private CourseDto course;
}
