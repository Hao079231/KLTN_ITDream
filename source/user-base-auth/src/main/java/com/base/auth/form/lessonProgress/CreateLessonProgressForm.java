package com.base.auth.form.lessonProgress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateLessonProgressForm {
  @NotNull(message = "lessonId cannot be null")
  @ApiModelProperty(name = "lessonId")
  private Long lessonId;
  @NotNull(message = "courseEnrollmentId cannot be null")
  @ApiModelProperty(name = "courseEnrollmentId")
  private Long courseEnrollmentId;
}
