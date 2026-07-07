package com.base.auth.form.simulation;

import com.base.auth.validation.SimulationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateSimulationStatusForm {
  @NotNull(message = "id không được trống")
  @ApiModelProperty(name = "id")
  private Long id;

  @SimulationStatus
  @ApiModelProperty(name = "status")
  private Integer status;

  @ApiModelProperty(name = "notice")
  private String notice;
}
