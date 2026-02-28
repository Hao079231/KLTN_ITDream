package com.base.auth.form.reviewSubmission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateCompleteReviewSubmissionForm {
  @NotNull(message = "courseId cannot be null")
  @ApiModelProperty(name = "courseId")
  private Long courseId;
  @NotEmpty(message = "studentUsername cannot be null")
  @ApiModelProperty(name = "studentUsername")
  private String studentUsername;
}
