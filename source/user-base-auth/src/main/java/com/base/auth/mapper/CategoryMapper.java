package com.base.auth.mapper;

import com.base.auth.dto.category.CategoryAutoCompleteDto;
import com.base.auth.dto.category.CategoryDto;
import com.base.auth.form.category.CreateCategoryForm;
import com.base.auth.form.category.UpdateCategoryForm;
import com.base.auth.model.Category;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  Category fromCreateCategoryFormToEntity(CreateCategoryForm createCategoryForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCategoryDto")
  CategoryDto fromEntityToCategoryDto(Category category);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCategoryAutoCompleteDto")
  CategoryAutoCompleteDto fromEntityToCategoryAutoCompleteDto(Category category);

  @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "fromEntityToCategoryDto")
  @BeanMapping(ignoreByDefault = true)
  List<CategoryDto> fromEntityToCategoryDtoList(List<Category> categories);

  @IterableMapping(elementTargetType = CategoryAutoCompleteDto.class, qualifiedByName = "fromEntityToCategoryAutoCompleteDto")
  @BeanMapping(ignoreByDefault = true)
  List<CategoryAutoCompleteDto> fromEntityToCategoryAutoCompleteDtoList(List<Category> categories);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  void fromUpdateCategoryFormToEntity(UpdateCategoryForm updateCategoryForm, @MappingTarget Category category);
}
