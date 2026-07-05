package com.base.auth.validation;

import com.base.auth.validation.impl.JobPostTypeValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobPostTypeValidation.class)
@Documented
public @interface JobPostType {
  boolean allowNull() default false;

  String message() default "Thể loại không hợp lệ: 1 - sự kiện, 2 - công việc, 3 - mạng lưới nhân tài";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
