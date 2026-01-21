package com.base.auth.form.lessonQuestion;

import com.base.auth.validation.QuestionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateLessonQuestionForm {
  @NotEmpty(message = "question cannot be null")
  @ApiModelProperty(name = "question")
  private String question;
  @NotNull(message = "question type cannot be null")
  @QuestionType
  private Integer questionType;
  @ApiModelProperty(name = "options")
  private String options;
  @NotNull(message = "lessonId cannot be null")
  @ApiModelProperty(name = "lessonId")
  private Long lessonId;
}
