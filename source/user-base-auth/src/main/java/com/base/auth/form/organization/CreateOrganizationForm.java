package com.base.auth.form.organization;

import com.base.auth.validation.OrganizationType;
import com.base.auth.validation.VietNamHotline;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class CreateOrganizationForm {
  @NotEmpty(message = "Tên tổ chức không được để trống")
  @ApiModelProperty(name = "name")
  private String name;
  @NotEmpty(message = "Tên viết tắt tổ chức không được để trống")
  @ApiModelProperty(name = "shortName")
  private String shortName;
  @NotEmpty(message = "Đường dẫn hình ảnh đại diện không được để trống")
  @ApiModelProperty(name = "logoUrl")
  private String logoUrl;
  @VietNamHotline
  @ApiModelProperty(name = "hotline")
  private String hotline;
  @OrganizationType
  @ApiModelProperty(name = "type")
  private Integer type;
}
