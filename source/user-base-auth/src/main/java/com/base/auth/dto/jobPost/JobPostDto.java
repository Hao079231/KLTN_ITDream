package com.base.auth.dto.jobPost;

import com.base.auth.dto.educator.ProfileEducatorDto;
import com.base.auth.dto.nation.NationDto;
import com.base.auth.dto.simulation.SimulationDisplayDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class JobPostDto {
  private Long id;

  private String title;

  private String content;

  private String image;

  private Integer type;

  private Integer roleType;

  private String jobUrl;

  private String address;

  private NationDto province;

  private NationDto ward;

  private String notice;

  private Date date;

  private Date endDate;

  private Integer status;

  @JsonIgnoreProperties({"category", "educator"})
  private List<SimulationDisplayDto> simulations;

  private ProfileEducatorDto educator;
}
