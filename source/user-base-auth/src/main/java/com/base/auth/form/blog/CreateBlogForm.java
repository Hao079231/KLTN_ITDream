package com.base.auth.form.blog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateBlogForm {
  @ApiModelProperty(name = "name")
  private String name;

  @ApiModelProperty(name = "subject")
  private String subject;

  @ApiModelProperty(name = "content")
  private String content;

  @ApiModelProperty(name = "image")
  private String image;

  @ApiModelProperty(name = "parentId")
  private Long parentId;

  @ApiModelProperty(name = "categoryId")
  private Long categoryId;
}
