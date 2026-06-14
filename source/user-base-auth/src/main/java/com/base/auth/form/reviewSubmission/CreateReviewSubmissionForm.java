package com.base.auth.form.reviewSubmission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateReviewSubmissionForm {
  @NotNull(message = "studentSubmissionId cannot be null")
  @ApiModelProperty(name = "studentSubmissionId")
  private Long studentSubmissionId;
  @NotNull(message = "studentTaskProgressId cannot be null")
  @ApiModelProperty(name = "studentTaskProgressId")
  private Long studentTaskProgressId;
  @NotEmpty(message = "review content cannot be null")
  @ApiModelProperty(name = "content")
  private String content;
}
