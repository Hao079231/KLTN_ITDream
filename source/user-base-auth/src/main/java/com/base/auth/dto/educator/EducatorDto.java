package com.base.auth.dto.educator;

import com.base.auth.dto.account.AccountDto;
import com.base.auth.dto.organization.OrganizationDto;
import lombok.Data;

@Data
public class EducatorDto {
  private Long id;
  private AccountDto account;
  private OrganizationDto organization;
}
