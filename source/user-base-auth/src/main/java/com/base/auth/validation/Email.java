package com.base.auth.validation;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.impl.EmailValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidation.class)
@Documented
public @interface Email {
  boolean allowNull() default false;

  String pattern() default ITDreamConstant.EMAIL_PATTERN;

  String message() default "Vui lòng nhập email hợp lệ, ví dụ: test@example.com";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
