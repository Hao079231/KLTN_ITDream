package com.base.auth.form.taskQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateTaskQuestionForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "Câu hỏi không được để trống")
  @ApiModelProperty(name = "question")
  private String question;
  @ApiModelProperty(name = "options")
  private String options;
}
