package com.base.auth.form.courseEnrollment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateCourseEnrollmentForm {
  @NotNull(message = "courseId cannot be null")
  @ApiModelProperty(name = "courseId")
  private Long courseId;
}
