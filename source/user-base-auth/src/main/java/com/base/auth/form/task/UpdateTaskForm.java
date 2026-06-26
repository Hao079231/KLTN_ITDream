package com.base.auth.form.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateTaskForm {
  @NotNull(message = "id không được để trống")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "Tiêu đề nhiệm vụ không được để trống")
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
  /**
   * Submission requirement (subtask only).
   * 0 = none, 1 = file only, 2 = text only, 3 = file + text.
   */
  @ApiModelProperty(name = "submissionType")
  private Integer submissionType;
}
