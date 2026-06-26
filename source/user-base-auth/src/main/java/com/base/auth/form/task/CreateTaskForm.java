package com.base.auth.form.task;

import com.base.auth.validation.TaskKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateTaskForm {
  @NotEmpty(message = "Tiêu đề nhiệm vụ không được để trống")
  @ApiModelProperty(name = "title")
  private String title;
  @ApiModelProperty(name = "description")
  private String description;
  @ApiModelProperty(name = "content")
  private String content;
  @TaskKind
  @ApiModelProperty(name = "kind")
  private Integer kind;
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
  @ApiModelProperty(name = "parentId")
  private Long parentId;
  @NotNull(message = "ID mô phỏng không được để trống")
  @ApiModelProperty(name = "simulationId")
  private Long simulationId;
}
