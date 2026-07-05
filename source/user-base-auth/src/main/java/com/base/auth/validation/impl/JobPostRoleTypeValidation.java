package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.JobPostRoleType;
import com.base.auth.validation.JobPostType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JobPostRoleTypeValidation implements ConstraintValidator<JobPostRoleType, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(JobPostRoleType constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.JOB_POST_ROLE_TYPES.contains(value);
  }
}
