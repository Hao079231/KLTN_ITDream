package com.base.auth.validation;

import com.base.auth.validation.impl.NationKindValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NationKindValidation.class)
@Documented
public @interface NationKind {
  boolean allowNull() default false;

  String message() default "Thể loại địa chỉ không hợp lệ: 1 - tỉnh / thành phố, 2 - xã / phường";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
