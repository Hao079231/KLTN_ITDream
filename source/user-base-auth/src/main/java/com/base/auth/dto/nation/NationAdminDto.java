package com.base.auth.dto.nation;

import com.base.auth.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class NationAdminDto extends ABasicAdminDto {
  private String name;
  private Integer kind;
  private NationAdminDto parent;
}
