package com.base.auth.dto.educator;

import com.base.auth.dto.account.ProfileAccountDto;
import com.base.auth.dto.organization.OrganizationClientDto;
import java.util.Date;
import lombok.Data;

@Data
public class ProfileEducatorDto {
  private ProfileAccountDto profileAccountDto;
  private OrganizationClientDto organization;
}
