package com.base.auth.validation;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.impl.EmailValidation;
import com.base.auth.validation.impl.VietNamHotlineValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VietNamHotlineValidation.class)
@Documented
public @interface VietNamHotline {
  boolean allowNull() default false;

  String pattern() default ITDreamConstant.VIETNAM_HOTLINE_PATTERN;

  String message() default "VietNam hotline invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
