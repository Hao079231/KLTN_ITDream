package com.base.auth.form.account;

import com.base.auth.validation.Email;
import com.base.auth.validation.Password;
import com.base.auth.validation.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.Past;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel
public class UpdateProfileAdminForm {
    @Email
    @ApiModelProperty(name = "email")
    private String email;
    @Phone(allowNull = true)
    @ApiModelProperty(name = "phone")
    private String phone;
    //    @Password(allowNull = true)
    @ApiModelProperty(name = "password")
    private String password;
    @ApiModelProperty(name = "oldPassword")
//    @NotEmpty(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;
    @NotEmpty(message = "Họ và tên không được để trống")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    @ApiModelProperty(name = "birthday")
    @Past(message = "Ngày sinh phải trong quá khứ")
    private Date birthday;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
}