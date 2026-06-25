package com.base.auth.form.reviewSubmission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateCompleteReviewSubmissionForm {
  @NotNull(message = "ID mô phỏng không được để trống")
  @ApiModelProperty(name = "simulationId")
  private Long simulationId;
  @NotEmpty(message = "Tên đăng nhập học viên không được để trống")
  @ApiModelProperty(name = "studentUsername")
  private String studentUsername;
}
