package com.base.auth.form.reviewSubmission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateReviewSubmissionForm {
  @NotNull(message = "correctAnswerId cannot be null")
  @ApiModelProperty(name = "correctAnswerId")
  private Long correctAnswerId;
  @NotEmpty(message = "student username cannot be null")
  @ApiModelProperty(name = "username")
  private String username;
  @NotEmpty(message = "review content cannot be null")
  @ApiModelProperty(name = "content")
  private String content;
}
