package com.base.auth.form.lesson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateLessonForm {
  @NotEmpty(message = "title cannot be null")
  @ApiModelProperty(name = "title")
  private String title;
  @ApiModelProperty(name = "introduction")
  private String introduction;
  @NotEmpty(message = "description cannot be null")
  @ApiModelProperty(name = "description")
  private String description;
  @ApiModelProperty(name = "content")
  private String content;
  @ApiModelProperty(name = "imagePath")
  private String imagePath;
  @ApiModelProperty(name = "filePath")
  private String filePath;
  @ApiModelProperty(name = "videoPath")
  private String videoPath;
  @NotNull(message = "chapterId cannot be null")
  @ApiModelProperty(name = "chapterId")
  private Long chapterId;
  @ApiModelProperty(name = "previousId")
  private Long previousId;
  @ApiModelProperty(name = "nextId")
  private Long nextId;
}
