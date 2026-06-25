package com.base.auth.validation;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.impl.PhoneValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidation.class)
@Documented
public @interface Phone {
  boolean allowNull() default false;

  String pattern() default ITDreamConstant.PHONE_PATTERN;

  String message() default "Số điện thoại phải bắt đầu từ 0 và có đúng 10 chữ số";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
