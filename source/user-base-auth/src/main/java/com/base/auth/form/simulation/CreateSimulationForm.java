package com.base.auth.form.simulation;

import com.base.auth.validation.SimulationLevel;
import com.base.auth.validation.TaskKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateSimulationForm {
  @NotEmpty(message = "Tiêu đề không được để trống")
  @ApiModelProperty(name = "title")
  private String title;
  @NotEmpty(message = "Tổng quan không được để trống")
  @ApiModelProperty(name = "overview")
  private String overview;
  @NotEmpty(message = "Mô tả không được để trống")
  @ApiModelProperty(name = "description")
  private String description;
  @SimulationLevel
  @ApiModelProperty(name = "level")
  private Integer level;
  @NotEmpty(message = "Thời gian dự kiến không được để trống")
  @ApiModelProperty(name = "duration")
  private String duration;
  @NotEmpty(message = "Hình đại diện không được để trống")
  @ApiModelProperty(name = "thumbnail")
  private String thumbnail;
  @ApiModelProperty(name = "videoPath")
  private String videoPath;
  @NotNull(message = "ID chuyên ngành không được để trống")
  @ApiModelProperty(name = "categoryId")
  private Long categoryId;
}
