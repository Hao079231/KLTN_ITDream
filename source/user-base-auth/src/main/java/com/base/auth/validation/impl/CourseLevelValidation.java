package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.CourseLevel;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CourseLevelValidation implements ConstraintValidator<CourseLevel, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(CourseLevel constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.COURSE_LEVELS.contains(value);
  }
}
