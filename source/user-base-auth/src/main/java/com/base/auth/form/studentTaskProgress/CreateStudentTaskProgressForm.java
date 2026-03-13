package com.base.auth.form.studentTaskProgress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateStudentTaskProgressForm {
  @NotNull(message = "taskId cannot be null")
  @ApiModelProperty(name = "taskId")
  private Long taskId;
  @NotNull(message = "simulationEnrollmentId cannot be null")
  @ApiModelProperty(name = "simulationEnrollmentId")
  private Long simulationEnrollmentId;
}
