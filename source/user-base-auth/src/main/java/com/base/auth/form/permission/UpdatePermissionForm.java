package com.base.auth.form.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdatePermissionForm {
  @NotEmpty(message = "id cannot be null")
  @ApiModelProperty(name = "id", required = true)
  private Long id;

  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name", required = true)
  private String name;

  @NotEmpty(message = "action cannot be null")
  @ApiModelProperty(name = "action", required = true)
  private String action;

  @NotNull(message = "showMenu cannot be null")
  @ApiModelProperty(name = "showMenu", required = true)
  private Boolean showMenu;

  @NotEmpty(message = "description cannot be null")
  @ApiModelProperty(name = "description", required = true)
  private String description;

  @NotEmpty(message = "nameGroup cannot be null")
  @ApiModelProperty(name = "nameGroup", required = true)
  private String nameGroup;

  @NotEmpty(message = "permissionCode cannot be null")
  @ApiModelProperty(name = "permissionCode", required = true)
  private String permissionCode;
}
