package com.base.auth.dto.simulation;

import com.base.auth.dto.category.CategoryAutoCompleteDto;
import com.base.auth.dto.educator.ProfileEducatorDto;
import lombok.Data;

@Data
public class SimulationClientDto {
  private Long id;
  private String title;
  private String overview;
  private String description;
  private Integer level;
  private String duration;
  private String thumbnail;
  private String videoPath;
  private Float avgStar;
  private Integer totalParticipant;
  private CategoryAutoCompleteDto category;
  private ProfileEducatorDto educator;
}
