package com.base.auth.validation;

import com.base.auth.validation.impl.JobPostRoleTypeValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobPostRoleTypeValidation.class)
@Documented
public @interface JobPostRoleType {
  boolean allowNull() default true;

  String message() default "Vị trí tuyển dụng không hợp lệ: 1 - thực tập sinh, 2 - bán thời gian, 3 - toàn thời gian, 4 - thực tập sinh, bán thời gian, 5 - thực tập sinh, toàn thời gian, 6 - bán thời gian, toàn thời gian, 7 - cả 3 vị trí";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
