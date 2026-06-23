package com.base.auth.form.taskQuestion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateTaskQuestionForm {
  @NotEmpty(message = "Câu hỏi không được để trống")
  @ApiModelProperty(name = "question")
  private String question;
  @ApiModelProperty(name = "options")
  private String options;
  @NotNull(message = "ID nhiệm vụ không được để trống")
  @ApiModelProperty(name = "taskId")
  private Long taskId;
}
