package com.base.auth.form.nation;

import com.base.auth.validation.NationKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class CreateNationForm {
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name", required = true)
  private String name;

  @NationKind
  @ApiModelProperty(name = "kind", required = true)
  private Integer kind;

  @ApiModelProperty(name = "parentId")
  private Long parentId;
}
