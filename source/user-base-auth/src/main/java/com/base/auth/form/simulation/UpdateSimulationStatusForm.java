package com.base.auth.form.simulation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateSimulationStatusForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotNull(message = "status không được để trống")
  @ApiModelProperty(name = "status")
  private Integer status;
  @ApiModelProperty(name = "notice")
  private String notice;
}
