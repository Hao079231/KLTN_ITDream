package com.base.auth.dto.chapter;

import com.base.auth.dto.course.CourseDisplayDto;
import lombok.Data;

@Data
public class ChapterDisplayDto {
  private Long id;
  private String name;
  private CourseDisplayDto course;
}
