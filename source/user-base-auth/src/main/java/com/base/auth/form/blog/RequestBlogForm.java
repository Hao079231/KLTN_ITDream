package com.base.auth.form.blog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class RequestBlogForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @ApiModelProperty(name = "notice")
  private String notice;
}
