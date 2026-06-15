package com.base.auth.form.student;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class StudentPreferencesForm {
  @ApiModelProperty(name = "organizationId")
  private Long organizationId;

  @ApiModelProperty(name = "specializationId")
  private Long specializationId;
}
