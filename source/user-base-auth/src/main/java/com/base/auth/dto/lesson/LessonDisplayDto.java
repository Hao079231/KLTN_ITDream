package com.base.auth.dto.lesson;

import com.base.auth.dto.course.CourseClientDto;
import lombok.Data;

@Data
public class LessonDisplayDto {
  private Long id;
  private String name;
  private String title;
  private String description;
  private String introduction;
  private Integer kind;
  private ParentLessonDto parent;
  private CourseClientDto simulation;
}
