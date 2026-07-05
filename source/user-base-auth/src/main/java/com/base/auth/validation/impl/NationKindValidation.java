package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.NationKind;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NationKindValidation implements ConstraintValidator<NationKind, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(NationKind constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.NATION_KINDS.contains(value);
  }
}
