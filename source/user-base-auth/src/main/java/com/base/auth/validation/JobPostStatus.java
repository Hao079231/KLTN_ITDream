package com.base.auth.validation;

import com.base.auth.validation.impl.JobPostStatusValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobPostStatusValidation.class)
@Documented
public @interface JobPostStatus {
  boolean allowNull() default false;

  String message() default "Trạng thái không hợp lệ: 1 - hoạt động, 0 - ẩn, -1 - chặn";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
