package com.base.auth.form.questionQuizHistory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateQuestionQuizHistoryForm {
  @NotEmpty(message = "answer cannot be null")
  @ApiModelProperty(name = "answer")
  private String answer;
  @NotNull(message = "isCorrect cannot be null")
  @ApiModelProperty(name = "isCorrect")
  private Boolean isCorrect;
  @NotNull(message = "lessonProgressId cannot be null")
  @ApiModelProperty(name = "lessonProgressId")
  private Long lessonProgressId;
  @NotNull(message = "lessonQuestionId cannot be null")
  @ApiModelProperty(name = "lessonQuestionId")
  private Long lessonQuestionId;
}
