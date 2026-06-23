package com.base.auth.form.achievement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateAchievementForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "Đường dẫn file không được để trống")
  @ApiModelProperty(name = "filePath")
  private String filePath;
}
