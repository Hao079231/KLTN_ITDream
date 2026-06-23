package com.base.auth.form.studentTaskProgress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateStudentTaskProgressForm {
  @NotNull(message = "ID nhiệm vụ không được để trống")
  @ApiModelProperty(name = "taskId")
  private Long taskId;
  @NotNull(message = "ID đăng ký mô phỏng không được để trống")
  @ApiModelProperty(name = "simulationEnrollmentId")
  private Long simulationEnrollmentId;
}
