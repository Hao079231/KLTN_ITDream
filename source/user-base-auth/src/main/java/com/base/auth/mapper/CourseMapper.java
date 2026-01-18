package com.base.auth.mapper;

import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.course.CourseClientDto;
import com.base.auth.dto.course.CourseDto;
import com.base.auth.form.course.CreateCourseForm;
import com.base.auth.form.course.UpdateCourseForm;
import com.base.auth.model.Course;
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
public interface CourseMapper {
  @Mapping(source = "title", target = "title")
  @Mapping(source = "overview", target = "overview")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "thumbnail", target = "thumbnail")
  @Mapping(source = "videoPath", target = "videoPath")
  @BeanMapping(ignoreByDefault = true)
  Course fromCreateCourseFormToEntity(CreateCourseForm createCourseForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "overview", target = "overview")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "thumbnail", target = "thumbnail")
  @Mapping(source = "videoPath", target = "videoPath")
  @Mapping(source = "avgStar", target = "avgStar")
  @Mapping(source = "totalParticipant", target = "totalParticipant")
  @Mapping(source = "notice", target = "notice")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEntityToEducatorDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCourseDto")
  CourseDto fromEntityToCourseDto(Course course);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "totalParticipant", target = "totalParticipant")
  @Mapping(source = "thumbnail", target = "thumbnail")
  @Mapping(source = "avgStar", target = "avgStar")
  @Mapping(source = "notice", target = "notice")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEducatorToProfileDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCourseDisplayDto")
  CourseDisplayDto fromEntityToCourseDisplayDto(Course course);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "overview", target = "overview")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "thumbnail", target = "thumbnail")
  @Mapping(source = "videoPath", target = "videoPath")
  @Mapping(source = "avgStar", target = "avgStar")
  @Mapping(source = "totalParticipant", target = "totalParticipant")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryAutoCompleteDto")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEducatorToProfileDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCourseClientDto")
  CourseClientDto fromEntityToCourseClientDto(Course course);

  @IterableMapping(elementTargetType = CourseDto.class, qualifiedByName = "fromEntityToCourseDto")
  List<CourseDto> fromEntityToCourseDtoList(List<Course> courses);

  @IterableMapping(elementTargetType = CourseDisplayDto.class, qualifiedByName = "fromEntityToCourseDisplayDto")
  List<CourseDisplayDto> fromEntityToCourseDisplayDtoList(List<Course> courses);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "overview", target = "overview")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "level", target = "level")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "thumbnail", target = "thumbnail")
  @Mapping(source = "videoPath", target = "videoPath")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateCourseFormToEntity(UpdateCourseForm updateCourseForm, @MappingTarget Course course);
}
