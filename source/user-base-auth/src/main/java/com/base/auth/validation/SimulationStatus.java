package com.base.auth.validation;

import com.base.auth.validation.impl.SimulationStatusValidation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SimulationStatusValidation.class)
@Documented
public @interface SimulationStatus {
  boolean allowNull() default false;

  String message() default "Trạng thái không hợp lệ: 0 - không hoạt động, 1 - hoạt động, 2 - chờ phê duyệt, 3 - chờ duyệt xóa, -1 - bị từ chối";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
