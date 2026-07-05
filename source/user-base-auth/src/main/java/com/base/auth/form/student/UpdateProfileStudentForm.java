package com.base.auth.form.student;

import com.base.auth.validation.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import lombok.Data;

@Data
@ApiModel
public class UpdateProfileStudentForm {
  @ApiModelProperty(name = "username")
  @NotEmpty(message = "Tên đăng nhập không được để trống")
  private String username;
  @ApiModelProperty(name = "fullname")
  @NotEmpty(message = "Họ và tên không được để trống")
  private String fullname;
  @ApiModelProperty(name = "birthday")
  @Past(message = "Ngày sinh phải ở trong quá khứ")
  private Date birthday;
//  @Phone
  private String phone;
  @ApiModelProperty(name = "avatarPath")
  private String avatarPath;
  @Valid
  @ApiModelProperty(name = "preferences")
  private List<StudentPreferencesForm> preferences;
}
