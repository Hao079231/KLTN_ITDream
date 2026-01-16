package com.base.auth.form.feedback;

import com.base.auth.validation.Star;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateFeedbackForm {
  @Star
  @ApiModelProperty(name = "star")
  private Integer star;
  @ApiModelProperty(name = "comment")
  private String comment;
  @NotNull(message = "simulationId cannot be null")
  @ApiModelProperty(name = "simulationId")
  private Long simulationId;
}
