package com.base.auth.form.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateGroupForm {
    @NotNull(message = "id không được để trống")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @NotNull(message = "Tên nhóm không được để trống")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @ApiModelProperty(name = "description")
    private String description;
    @NotNull(message = "Danh sách quyền không được để trống")
    @ApiModelProperty(name = "permissions", required = true)
    private Long[] permissions;
}
