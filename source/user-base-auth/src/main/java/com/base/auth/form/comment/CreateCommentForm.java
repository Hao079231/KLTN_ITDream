package com.base.auth.form.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateCommentForm {
  @NotEmpty(message = "content cannot be null")
  @ApiModelProperty(name = "content")
  private String content;
  @NotNull(message = "lessonId cannot be null")
  @ApiModelProperty(name = "lessonId")
  private Long lessonId;
  @ApiModelProperty(name = "parentId")
  private Long parentId;
}
