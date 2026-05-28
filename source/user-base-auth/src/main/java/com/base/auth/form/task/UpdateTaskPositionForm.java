package com.base.auth.form.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateTaskPositionForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @NotNull(message = "newOrder cannot be null")
  @ApiModelProperty(name = "newOrder")
  private Integer newOrder;

  private Long newParentId;

  @ApiModelProperty(name = "isShowSimulation")
  private Boolean isShowSimulation;
}
