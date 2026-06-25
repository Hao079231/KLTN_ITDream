package com.base.auth.form.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreateGroupForm {
    @NotEmpty(message = "Tên nhóm không được để trống")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @NotEmpty(message = "Mô tả nhóm không được để trống")
    @ApiModelProperty(name = "description", required = true)
    private String description;
    @NotNull(message = "Danh sách quyền không được để trống")
    @ApiModelProperty(name = "permissions", required = true)
    private Long[] permissions;
    @NotNull(message = "Thể loại nhóm không được để trống")
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;
}
