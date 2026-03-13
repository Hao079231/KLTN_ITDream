package com.base.auth.dto.simulation;

import com.base.auth.dto.educator.ProfileEducatorDto;
import lombok.Data;

@Data
public class SimulationDisplayDto {
  private Long id;
  private String title;
  private Integer level;
  private String duration;
  private Integer totalParticipant;
  private String thumbnail;
  private Float avgStar;
  private String notice;
  private int status;
  private ProfileEducatorDto educator;
}
