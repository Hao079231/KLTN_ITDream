package com.base.auth.form.jobPost;

import com.base.auth.validation.JobPostStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class JobPostStatusForm {
  @NotNull(message = "id không được trống")
  @ApiModelProperty(name = "id")
  private Long id;

  @ApiModelProperty(name = "notice")
  private String notice;

  @JobPostStatus
  @ApiModelProperty(name = "status")
  private Integer status;
}
