package com.base.auth.form.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateCommentForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "Nội dung không được để trống")
  @ApiModelProperty(name = "content")
  private String content;
}
