package com.base.auth.dto.course;

import com.base.auth.dto.category.CategoryDto;
import com.base.auth.dto.educator.EducatorDto;
import lombok.Data;

@Data
public class CourseDto {
  private Long id;
  private String title;
  private String overview;
  private String description;
  private Integer level;
  private Integer type;
  private String duration;
  private String thumbnail;
  private String videoPath;
  private Float avgStar;
  private Integer totalParticipant;
  private String notice;
  private int status;
  private CategoryDto category;
  private EducatorDto educator;
}
