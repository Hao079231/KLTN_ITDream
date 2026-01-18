package com.base.auth.mapper;

import com.base.auth.dto.lesson.LessonDisplayDto;
import com.base.auth.dto.lesson.LessonDto;
import com.base.auth.dto.lesson.LessonEducatorDto;
import com.base.auth.dto.lesson.LessonStudentDto;
import com.base.auth.form.lesson.CreateLessonForm;
import com.base.auth.form.lesson.UpdateLessonForm;
import com.base.auth.model.Lesson;
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
    uses = {ChapterMapper.class})
public interface LessonMapper {
  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "imagePath", target = "imagePath")
  @Mapping(source = "filePath", target = "filePath")
  @Mapping(source = "videoPath", target = "videoPath")
  @BeanMapping(ignoreByDefault = true)
  Lesson fromCreateLessonFormToEntity(CreateLessonForm createLessonForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "chapter", target = "chapter", qualifiedByName = "fromEntityToChapterDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonDisplayDto")
  LessonDisplayDto fromEntityToLessonDisplayDto(Lesson lesson);

  @IterableMapping(elementTargetType = LessonDisplayDto.class, qualifiedByName = "fromEntityToLessonDisplayDto")
  List<LessonDisplayDto> fromEntityToLessonDisplayDtoList(List<Lesson> lesson);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "imagePath", target = "imagePath")
  @Mapping(source = "filePath", target = "filePath")
  @Mapping(source = "videoPath", target = "videoPath")
  @Mapping(source = "totalError", target = "totalError")
  @Mapping(source = "totalQuestion", target = "totalQuestion")
  @Mapping(source = "chapter", target = "chapter", qualifiedByName = "fromEntityToChapterDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonDto")
  LessonDto fromEntityToLessonDto(Lesson lesson);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "chapter", target = "chapter", qualifiedByName = "fromEntityToCourseDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonDisplayAdminDto")
  LessonDto fromEntityToLessonDisplayAdminDto(Lesson lesson);

  @IterableMapping(elementTargetType = LessonDto.class, qualifiedByName = "fromEntityToLessonDisplayAdminDto")
  List<LessonDto> fromEntityToLessonDtoList(List<Lesson> lesson);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "imagePath", target = "imagePath")
  @Mapping(source = "filePath", target = "filePath")
  @Mapping(source = "videoPath", target = "videoPath")
  @Mapping(source = "chapter", target = "chapter", qualifiedByName = "fromEntityToChapterDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonStudentDto")
  LessonStudentDto fromEntityToLessonStudentDto(Lesson lesson);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "imagePath", target = "imagePath")
  @Mapping(source = "filePath", target = "filePath")
  @Mapping(source = "videoPath", target = "videoPath")
  @Mapping(source = "totalError", target = "totalError")
  @Mapping(source = "totalQuestion", target = "totalQuestion")
  @Mapping(source = "chapter", target = "chapter", qualifiedByName = "fromEntityToChapterDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonEducatorDto")
  LessonEducatorDto fromEntityToLessonEducatorDto(Lesson lesson);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "introduction", target = "introduction")
  @Mapping(source = "description", target = "description")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateLessonFormToEntity(UpdateLessonForm updateLessonForm, @MappingTarget Lesson lesson);
}
