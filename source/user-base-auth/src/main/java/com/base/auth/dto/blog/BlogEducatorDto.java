package com.base.auth.dto.blog;

import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.category.CategoryAutoCompleteDto;
import com.base.auth.dto.educator.ProfileEducatorDto;
import java.util.List;
import lombok.Data;

@Data
public class BlogEducatorDto{
  private Long id;

  private String name;

  private String subject;

  private String content;

  private String image;

  private String notice;

  private Integer status;

  private BlogEducatorDto parent;

  private CategoryAutoCompleteDto category;

  private ProfileEducatorDto educator;

  private ResponseListDto<List<BlogEducatorDto>> subjects;
}
