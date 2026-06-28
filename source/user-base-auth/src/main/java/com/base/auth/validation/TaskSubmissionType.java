package com.base.auth.validation;

import com.base.auth.validation.impl.TaskSubmissionTypeValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskSubmissionTypeValidation.class)
@Documented
public @interface TaskSubmissionType {
  boolean allowNull() default false;

  String message() default "Thể loại nộp không hợp lệ: 0 - Không nộp, 1 - Chỉ nộp tệp, 2 - Chỉ nộp văn bản, 3 - Nộp tệp, văn bản";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}