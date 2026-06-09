package com.base.auth.form.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateTaskForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;
  @NotEmpty(message = "title cannot be null")
  @ApiModelProperty(name = "title")
  private String title;
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
}
