package com.base.auth.validation.impl;

import com.base.auth.utils.FormatUtils;
import com.base.auth.validation.VietNamHotline;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class VietNamHotlineValidation implements ConstraintValidator<VietNamHotline, String> {
  private boolean allowNull;
  private String pattern;

  @Override
  public void initialize(VietNamHotline constraintAnnotation) {
    allowNull = constraintAnnotation.allowNull();
    pattern = constraintAnnotation.pattern();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    String standardPhone = FormatUtils.convertHotlineToStandardizedPhone(value);
    return StringUtils.isBlank(standardPhone) ? allowNull : StringUtils.isNotBlank(standardPhone) && standardPhone.matches(pattern);
  }
}
