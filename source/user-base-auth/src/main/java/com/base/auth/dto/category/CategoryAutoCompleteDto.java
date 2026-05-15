package com.base.auth.dto.category;

import lombok.Data;

@Data
public class CategoryAutoCompleteDto {
  private Long id;
  private String name;
  private Integer kind;
}
