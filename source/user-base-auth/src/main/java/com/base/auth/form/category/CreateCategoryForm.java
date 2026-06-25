package com.base.auth.form.category;

import com.base.auth.validation.CategoryKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class CreateCategoryForm {
  @NotEmpty(message = "Tên dang mục không được để trống")
  @ApiModelProperty(name = "name")
  private String name;

  @CategoryKind
  @ApiModelProperty(name = "kind")
  private Integer kind;
}
