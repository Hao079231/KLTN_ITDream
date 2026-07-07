package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.SimulationStatus;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SimulationStatusValidation implements ConstraintValidator<SimulationStatus, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(SimulationStatus constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.JOB_POST_STATUSES.contains(value);
  }
}
