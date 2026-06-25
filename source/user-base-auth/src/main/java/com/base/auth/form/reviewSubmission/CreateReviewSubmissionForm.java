package com.base.auth.form.reviewSubmission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateReviewSubmissionForm {
  @NotNull(message = "ID bài nộp của học viên không được để trống")
  @ApiModelProperty(name = "studentSubmissionId")
  private Long studentSubmissionId;
  @NotNull(message = "ID tiến trình của học viên không được để trống")
  @ApiModelProperty(name = "studentTaskProgressId")
  private Long studentTaskProgressId;
  @NotEmpty(message = "Nội dung đánh giá không được để trống")
  @ApiModelProperty(name = "content")
  private String content;
}
