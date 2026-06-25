package com.base.auth.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.Past;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateAccountAdminForm {

    @NotNull(message = "id không được để trống")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @ApiModelProperty(name = "email")
    private String email;
    @ApiModelProperty(name = "phone")
    private String phone;
    @ApiModelProperty(name = "password")
    private String password;
    @NotEmpty(message = "Họ và tên không được để trống")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    @ApiModelProperty(name = "birthday")
    @Past(message = "Ngày sinh phải trong quá khứ")
    private Date birthday;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath ;
    @NotNull(message = "ID nhóm không được để trống")
    @ApiModelProperty(name = "groupId", required = true)
    private Long groupId;
    @NotNull(message = "Trạng thái không được để trống")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
}