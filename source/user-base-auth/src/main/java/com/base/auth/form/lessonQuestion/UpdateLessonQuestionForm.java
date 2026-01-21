package com.base.auth.form.lessonQuestion;

import com.base.auth.validation.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateLessonQuestionForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "question cannot be null")
  @ApiModelProperty(name = "question")
  private String question;
  @QuestionType
  @ApiModelProperty(name = "questionType")
  private Integer questionType;
  @ApiModelProperty(name = "options")
  private String options;
}
