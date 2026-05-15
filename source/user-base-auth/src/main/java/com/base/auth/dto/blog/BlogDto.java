package com.base.auth.dto.blog;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.category.CategoryDto;
import com.base.auth.dto.educator.EducatorDto;
import java.util.List;
import lombok.Data;

@Data
public class BlogDto extends ABasicAdminDto {
  private String name;

  private String subject;

  private String content;

  private String image;

  private String notice;

  private BlogDto parent;

  private CategoryDto category;

  private EducatorDto educator;

  private ResponseListDto<List<BlogDto>> subjects;
}
