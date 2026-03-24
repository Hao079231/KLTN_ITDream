package com.base.auth.form.simulation;

import com.base.auth.validation.TaskKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateSimulationForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "title cannot be null")
  @ApiModelProperty(name = "title")
  private String title;
  @NotEmpty(message = "overview cannot be null")
  @ApiModelProperty(name = "overview")
  private String overview;
  @NotEmpty(message = "description cannot be null")
  @ApiModelProperty(name = "description")
  private String description;
  @ApiModelProperty(name = "level")
  private Integer level;
  @NotEmpty(message = "duration cannot be null")
  @ApiModelProperty(name = "duration")
  private String duration;
  @NotEmpty(message = "thumbnail cannot be null")
  @ApiModelProperty(name = "thumbnail")
  private String thumbnail;
  @NotEmpty(message = "videoPath cannot be null")
  @ApiModelProperty(name = "videoPath")
  private String videoPath;
  @NotNull(message = "categoryId cannot be null")
  @ApiModelProperty(name = "categoryId")
  private Long categoryId;
}
