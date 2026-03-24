package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.OrganizationType;
import com.base.auth.validation.TaskKind;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OrganizationTypeValidation implements ConstraintValidator<OrganizationType, Integer> {
  private boolean allowNull;
  @Override
  public void initialize(OrganizationType constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.ORGANIZATION_TYPES.contains(value);
  }
}
