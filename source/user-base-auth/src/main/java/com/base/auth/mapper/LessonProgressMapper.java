package com.base.auth.mapper;

import com.base.auth.dto.lessonProgress.LessonProgressDisplayDto;
import com.base.auth.dto.lessonProgress.LessonProgressDto;
import com.base.auth.model.LessonProgress;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CourseEnrollmentMapper.class, LessonMapper.class})
public interface LessonProgressMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "errorCount", target = "errorCount")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "lesson", target = "lesson", qualifiedByName = "fromEntityToLessonDto")
  @Mapping(source = "courseEnrollment", target = "courseEnrollment", qualifiedByName = "fromEntityToCourseEnrollmentDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonProgressDto")
  LessonProgressDto fromEntityToLessonProgressDto(LessonProgress lessonProgress);

  @IterableMapping(elementTargetType = LessonProgressDto.class, qualifiedByName = "fromEntityToLessonProgressDto")
  List<LessonProgressDto> fromEntityToLessonProgressDtoList(List<LessonProgress> lessonProgressList);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "errorCount", target = "errorCount")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "lesson", target = "lesson", qualifiedByName = "fromEntityToLessonDisplayDto")
  @Mapping(source = "courseEnrollment", target = "courseEnrollment", qualifiedByName = "fromEntityToCourseEnrollmentDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonProgressDisplayDto")
  LessonProgressDisplayDto fromEntityToLessonProgressDisplayDto(LessonProgress lessonProgress);

  @IterableMapping(elementTargetType = LessonProgressDisplayDto.class, qualifiedByName = "fromEntityToLessonProgressDisplayDto")
  List<LessonProgressDisplayDto> fromEntityToLessonProgressDisplayDtoList(List<LessonProgress> lessonProgressList);
}
