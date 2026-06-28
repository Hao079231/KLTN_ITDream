package com.base.auth.validation.impl;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.TaskSubmissionType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TaskSubmissionTypeValidation implements ConstraintValidator<TaskSubmissionType, Integer> {
  private boolean allowNull;
  @Override
  public void initialize(TaskSubmissionType constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : ITDreamConstant.TASK_SUBMISSION_TYPES.contains(value);
  }
}
