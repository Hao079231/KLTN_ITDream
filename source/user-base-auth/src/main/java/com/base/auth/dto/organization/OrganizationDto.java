package com.base.auth.dto.organization;

import com.base.auth.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class OrganizationDto extends ABasicAdminDto {
  private String name;
  private String shortName;
  private String logoUrl;
  private String hotline;
  private Integer type;
}
