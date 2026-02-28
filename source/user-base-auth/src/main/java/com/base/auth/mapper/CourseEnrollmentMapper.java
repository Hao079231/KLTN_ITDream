package com.base.auth.mapper;

import com.base.auth.dto.courseEnrollment.CourseEnrollmentDisplayDto;
import com.base.auth.dto.courseEnrollment.CourseEnrollmentDto;
import com.base.auth.dto.courseEnrollment.StudentLessonViewsDto;
import com.base.auth.model.CourseEnrollment;
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
    uses = {CourseMapper.class, StudentMapper.class})
public interface CourseEnrollmentMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "progress", target = "progress")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromEntityToStudentDto")
  @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCourseEnrollmentDto")
  CourseEnrollmentDto fromEntityToCourseEnrollmentDto(CourseEnrollment courseEnrollment);

  @IterableMapping(elementTargetType = CourseEnrollmentDto.class, qualifiedByName = "fromEntityToCourseEnrollmentDto")
  List<CourseEnrollmentDto> fromEntityToCourseEnrollmentDtoList(List<CourseEnrollment> courseEnrollment);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "progress", target = "progress")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToProfileDto")
  @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCourseEnrollmentDisplayDto")
  CourseEnrollmentDisplayDto fromEntityToCourseEnrollmentDisplayDto(CourseEnrollment courseEnrollment);

  @IterableMapping(elementTargetType = CourseEnrollmentDisplayDto.class, qualifiedByName = "fromEntityToCourseEnrollmentDisplayDto")
  List<CourseEnrollmentDisplayDto> fromEntityToCourseEnrollmentDisplayDtoList(List<CourseEnrollment> courseEnrollment);

  @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToProfileDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToStudentLessonViewsDto")
  StudentLessonViewsDto fromEntityToStudentLessonViewsDto(CourseEnrollment courseEnrollment);

  @IterableMapping(elementTargetType = StudentLessonViewsDto.class, qualifiedByName = "fromEntityToStudentLessonViewsDto")
  List<StudentLessonViewsDto> fromEntityToStudentLessonViewsDtoList(List<CourseEnrollment> courseEnrollment);

}
