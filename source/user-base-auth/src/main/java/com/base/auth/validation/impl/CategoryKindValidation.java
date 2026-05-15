package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.CategoryKind;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryKindValidation implements ConstraintValidator<CategoryKind, Integer> {
  private boolean allowNull;
  @Override
  public void initialize(CategoryKind constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.CATEGORY_KINDS.contains(value);
  }
}
