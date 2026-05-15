package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.TaskType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TaskTypeValidation implements ConstraintValidator<TaskType, Integer> {
  private boolean allowNull;
  @Override
  public void initialize(TaskType constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.TASK_TYPES.contains(value);
  }
}
