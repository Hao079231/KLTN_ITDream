package com.base.auth.form.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreatePermissionForm {

    @NotEmpty(message = "Tên quyền không được để trống")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @NotEmpty(message = "Hành động quyền không được để trống")
    @ApiModelProperty(name = "action", required = true)
    private String action;
    @NotNull(message = "Hiển thị menu không được để trống")
    @ApiModelProperty(name = "showMenu", required = true)
    private Boolean showMenu;
    @NotEmpty(message = "Mô tả quyền không được để trống")
    @ApiModelProperty(name = "description", required = true)
    private String description;
    @NotEmpty(message = "Tên nhóm không được để trống")
    @ApiModelProperty(name = "nameGroup", required = true)
    private String nameGroup;

    @NotEmpty(message = "Mã quyền không được để trống")
    @ApiModelProperty(name = "permissionCode", required = true)
    private String permissionCode;
}
