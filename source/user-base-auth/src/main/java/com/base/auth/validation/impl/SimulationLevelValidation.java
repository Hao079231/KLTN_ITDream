package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.SimulationLevel;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SimulationLevelValidation implements ConstraintValidator<SimulationLevel, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(SimulationLevel constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.SIMULATION_LEVELS.contains(value);
  }
}
