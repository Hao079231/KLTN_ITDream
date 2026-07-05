package com.base.auth.dto.organization;

import lombok.Data;

@Data
public class OrganizationClientDto {
  private Long id;
  private String name;
  private String shortName;
  private String logoUrl;
  private String hotline;
  private Integer type;
}
