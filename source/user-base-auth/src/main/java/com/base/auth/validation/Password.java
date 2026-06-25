package com.base.auth.validation;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.validation.impl.PasswordValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidation.class)
@Documented
public @interface Password {
  boolean allowNull() default false;

  String pattern() default ITDreamConstant.PASSWORD_PATTERN;

  String message() default "Mật khẩu phải có từ 8 đến 15 ký tự, có ít nhất một ký tự hoa, ký tự thường, ký tự số, ký tự đặc biệt";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
