package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.JobPostStatus;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class JobPostStatusValidation implements ConstraintValidator<JobPostStatus, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(JobPostStatus constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.JOB_POST_STATUSES.contains(value);
  }
}
