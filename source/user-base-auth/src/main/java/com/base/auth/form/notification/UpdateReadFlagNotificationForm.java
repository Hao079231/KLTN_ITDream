package com.base.auth.form.notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateReadFlagNotificationForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id")
  private Long id;
}
