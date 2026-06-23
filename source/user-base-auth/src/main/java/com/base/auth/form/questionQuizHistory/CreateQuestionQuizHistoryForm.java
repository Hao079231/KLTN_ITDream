package com.base.auth.form.questionQuizHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateQuestionQuizHistoryForm {
  @NotEmpty(message = "Câu trả lời không được để trống")
  @ApiModelProperty(name = "answer")
  private String answer;
  @NotNull(message = "Việc đúng sai không được để trống")
  @ApiModelProperty(name = "isCorrect")
  private Boolean isCorrect;
  @NotNull(message = "ID tiến trình nhiệm vụ của học viên không được để trống")
  @ApiModelProperty(name = "studentTaskProgressId")
  private Long studentTaskProgressId;
  @ApiModelProperty(name = "taskQuestionId")
  private Long taskQuestionId;
}
