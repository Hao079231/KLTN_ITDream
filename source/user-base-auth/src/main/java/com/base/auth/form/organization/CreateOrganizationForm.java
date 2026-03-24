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
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;
  @NotEmpty(message = "shortName cannot be null")
  @ApiModelProperty(name = "shortName")
  private String shortName;
  @NotEmpty(message = "logoUrl cannot be null")
  @ApiModelProperty(name = "logoUrl")
  private String logoUrl;
  @VietNamHotline
  @ApiModelProperty(name = "hotline")
  private String hotline;
  @OrganizationType
  @ApiModelProperty(name = "type")
  private Integer type;
}
