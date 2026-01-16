package com.base.auth.form.lessonProgress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class RequestLessonProgressForm {
  @NotNull(message = "task id cannot be null")
  @ApiModelProperty(name = "task id")
  private Long taskId;
}
