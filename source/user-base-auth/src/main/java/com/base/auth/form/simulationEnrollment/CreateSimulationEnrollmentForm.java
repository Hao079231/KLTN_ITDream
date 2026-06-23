package com.base.auth.form.simulationEnrollment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateSimulationEnrollmentForm {
  @NotNull(message = "ID mô phỏng không được để trống")
  @ApiModelProperty(name = "simulationId")
  private Long simulationId;
}
