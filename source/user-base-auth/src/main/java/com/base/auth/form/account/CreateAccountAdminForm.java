package com.base.auth.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.Past;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreateAccountAdminForm {
    @NotEmpty(message = "Tên đăng nhập không được trống")
    @ApiModelProperty(name = "username", required = true)
    private String username;
    @ApiModelProperty(name = "email")
    @Email
    private String email;
    @ApiModelProperty(name = "phone")
    private String phone;
    @NotEmpty(message = "Mật khẩu không được trống")
    @ApiModelProperty(name = "password", required = true)
    private String password;
    @NotNull(message = "Vai trò không được trống")
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;
    @NotEmpty(message = "Họ và tên không được trống")
    @ApiModelProperty(name = "fullName",example = "Trung Hao",required = true)
    private String fullName;
    @ApiModelProperty(name = "birthday")
    @Past(message = "Ngày sinh phải là ở quá khứ")
    private Date birthday;
    private String avatarPath;
    @NotNull(message = "Trạng thái không được trống")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
    @NotNull(message = "ID nhóm không được trống")
    @ApiModelProperty(name = "groupId", required = true)
    private Long groupId;

}
