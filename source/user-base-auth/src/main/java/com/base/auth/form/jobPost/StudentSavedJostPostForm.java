package com.base.auth.form.jobPost;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class StudentSavedJostPostForm {
  @NotNull(message = "jobPostIds không được trống")
  @ApiModelProperty(name = "jobPostIds")
  private List<Long> jobPostIds;
}
