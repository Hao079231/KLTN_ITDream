package com.base.auth.dto.jobPost;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.educator.EducatorDto;
import com.base.auth.dto.nation.NationAdminDto;
import com.base.auth.dto.simulation.SimulationDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class JobPostAdminDto extends ABasicAdminDto {
  private String title;

  private String content;

  private String image;

  private Integer type;

  private Integer roleType;

  private String jobUrl;

  private String address;

  private NationAdminDto province;

  private NationAdminDto ward;

  private Date date;

  private Date endDate;

  @JsonIgnoreProperties({"category", "educator"})
  private List<SimulationDto> simulations;

  private EducatorDto educator;

  private String notice;
}
