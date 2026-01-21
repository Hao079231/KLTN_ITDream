package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.CourseType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CourseTypeValidation implements ConstraintValidator<CourseType, Integer> {
  private boolean allowNull;
  @Override
  public void initialize(CourseType constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.COURSE_TYPES.contains(value);
  }
}
