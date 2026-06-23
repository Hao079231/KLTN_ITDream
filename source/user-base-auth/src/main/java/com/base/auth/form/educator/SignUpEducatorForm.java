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
public class SignUpEducatorForm {
  @ApiModelProperty(name = "username", required = true)
  @NotEmpty(message = "Tên đăng nhập không được để trống")
  private String username;
  @ApiModelProperty(name = "email", required = true)
  @Email
  private String email;
  @ApiModelProperty(name = "phone", required = true)
  @Phone
  private String phone;
  @ApiModelProperty(name = "password", required = true)
  @Password
  private String password;
  @NotEmpty(message = "Họ và tên không được để trống")
  @ApiModelProperty(name = "fullName",example = "Hao Trinh", required = true)
  private String fullName;
  @ApiModelProperty(name = "birthday")
  @Past(message = "Ngày sinh phải ở trong quá khứ")
  private Date birthday;
  @NotNull(message = "ID tổ chức không được để trống")
  @ApiModelProperty(name = "organizationId")
  private Long organizationId;
}
