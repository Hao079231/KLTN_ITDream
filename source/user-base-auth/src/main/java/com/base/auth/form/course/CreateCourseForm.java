package com.base.auth.form.course;

import com.base.auth.validation.CourseLevel;
import com.base.auth.validation.CourseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateCourseForm {
  @NotEmpty(message = "title cannot be null")
  @ApiModelProperty(name = "title")
  private String title;
  @NotEmpty(message = "overview cannot be null")
  @ApiModelProperty(name = "overview")
  private String overview;
  @NotEmpty(message = "description cannot be null")
  @ApiModelProperty(name = "description")
  private String description;
  @CourseLevel
  @ApiModelProperty(name = "level")
  private Integer level;
  @CourseType
  @ApiModelProperty(name = "type")
  private Integer type;
  @NotEmpty(message = "duration cannot be null")
  @ApiModelProperty(name = "duration")
  private String duration;
  private String thumbnail;
  private String videoPath;
  @NotNull(message = "categoryId cannot be null")
  @ApiModelProperty(name = "categoryId")
  private Long categoryId;
}
