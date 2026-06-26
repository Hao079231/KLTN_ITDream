package com.base.auth.form.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateCommentForm {
  @NotEmpty(message = "Nội dung không được để trống")
  @ApiModelProperty(name = "content")
  private String content;
  @NotNull(message = "ID nhiệm vụ không được để trống")
  @ApiModelProperty(name = "taskId")
  private Long taskId;
  @ApiModelProperty(name = "parentId")
  private Long parentId;
  @ApiModelProperty(name = "simulationEnrollmentId")
  private Long simulationEnrollmentId;
}
