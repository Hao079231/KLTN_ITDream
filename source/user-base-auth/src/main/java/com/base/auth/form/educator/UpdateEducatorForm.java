package com.base.auth.form.educator;

import com.base.auth.validation.Email;
import com.base.auth.validation.Password;
import com.base.auth.validation.Phone;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import lombok.Data;

@Data
public class UpdateEducatorForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id", required = true)
  private Long id;
  @ApiModelProperty(name = "username")
  @NotEmpty(message = "Tên đăng nhập không được để trống")
  private String username;
  @ApiModelProperty(name = "fulName")
  @NotEmpty(message = "Họ và tên không được để trống")
  private String fullName;
  @Phone
  @ApiModelProperty(name = "phone")
  private String phone;
  @Email
  @ApiModelProperty(name = "email")
  private String email;
  @Password(allowNull = true)
  @ApiModelProperty(name = "password")
  private String password;
  @ApiModelProperty(name = "birthday")
  @Past(message = "Ngày sinh phải ở trong quá khứ")
  private Date birthday;
  @ApiModelProperty(name = "avatarPath")
  private String avatarPath;
  @ApiModelProperty(name = "status")
  private Integer status;
}
