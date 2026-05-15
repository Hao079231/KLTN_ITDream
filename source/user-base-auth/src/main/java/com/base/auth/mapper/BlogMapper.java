package com.base.auth.mapper;

import com.base.auth.dto.blog.BlogDto;
import com.base.auth.dto.blog.BlogEducatorDto;
import com.base.auth.dto.blog.BlogStudentDto;
import com.base.auth.form.blog.CreateBlogForm;
import com.base.auth.form.blog.UpdateBlogForm;
import com.base.auth.model.Blog;
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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CategoryMapper.class, EducatorMapper.class})
public interface BlogMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "subject", target = "subject")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @BeanMapping(ignoreByDefault = true)
  Blog fromCreateBlogFormToEntity(CreateBlogForm createBlogForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "subject", target = "subject")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "notice", target = "notice")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToBlogDto")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEntityToEducatorDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBlogDto")
  BlogDto fromEntityToBlogDto(Blog blog);

  @IterableMapping(elementTargetType = BlogDto.class, qualifiedByName = "fromEntityToBlogDto")
  List<BlogDto> fromEntityToBlogDtoList(List<Blog> blogs);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "subject", target = "subject")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "notice", target = "notice")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToBlogEducatorDto")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryAutoCompleteDto")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEducatorToProfileDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBlogEducatorDto")
  BlogEducatorDto fromEntityToBlogEducatorDto(Blog blog);

  @IterableMapping(elementTargetType = BlogEducatorDto.class, qualifiedByName = "fromEntityToBlogEducatorDto")
  List<BlogEducatorDto> fromEntityToBlogEducatorDtoList(List<Blog> blogs);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "subject", target = "subject")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToBlogEducatorDto")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryAutoCompleteDto")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEducatorToProfileDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBlogStudentDto")
  BlogStudentDto fromEntityToBlogStudentDto(Blog blog);

  @IterableMapping(elementTargetType = BlogStudentDto.class, qualifiedByName = "fromEntityToBlogStudentDto")
  List<BlogStudentDto> fromEntityToBlogStudentDtoList(List<Blog> blogs);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "subject", target = "subject")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateBlogFormToEntity(UpdateBlogForm updateBlogForm, @MappingTarget Blog blog);
}
