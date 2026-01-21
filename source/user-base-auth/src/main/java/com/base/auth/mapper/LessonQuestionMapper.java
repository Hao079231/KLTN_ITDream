package com.base.auth.mapper;

import com.base.auth.dto.lessonQuestion.LessonQuestionDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionEducatorDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionStudentDto;
import com.base.auth.form.lessonQuestion.CreateLessonQuestionForm;
import com.base.auth.form.lessonQuestion.UpdateLessonQuestionForm;
import com.base.auth.model.LessonQuestion;
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
    uses = {LessonMapper.class})
public interface LessonQuestionMapper {
  @Mapping(source = "question", target = "question")
  @Mapping(source = "questionType", target = "questionType")
  @Mapping(source = "options", target = "options")
  @BeanMapping(ignoreByDefault = true)
  LessonQuestion fromCreateLessonQuestionFormToEntity(CreateLessonQuestionForm createLessonQuestionForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "question", target = "question")
  @Mapping(source = "questionType", target = "questionType")
  @Mapping(source = "options", target = "options")
  @Mapping(source = "lesson", target = "lesson", qualifiedByName = "fromEntityToLessonDisplayAdminDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonQuestionDto")
  LessonQuestionDto fromEntityToLessonQuestionDto(LessonQuestion lessonQuestion);

  @IterableMapping(elementTargetType = LessonQuestionDto.class, qualifiedByName = "fromEntityToLessonQuestionDto")
  List<LessonQuestionDto> fromEntityToLessonQuestionDtoList(List<LessonQuestion> questions);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "question", target = "question")
  @Mapping(source = "questionType", target = "questionType")
  @Mapping(source = "options", target = "options")
  @Mapping(source = "lesson", target = "lesson", qualifiedByName = "fromEntityToLessonEducatorDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonQuestionEducatorDto")
  LessonQuestionEducatorDto fromEntityToLessonQuestionEducatorDto(LessonQuestion lessonQuestion);

  @IterableMapping(elementTargetType = LessonQuestionEducatorDto.class, qualifiedByName = "fromEntityToLessonQuestionEducatorDto")
  List<LessonQuestionEducatorDto> fromEntityToLessonQuestionEducatorDtoList(List<LessonQuestion> questions);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "question", target = "question")
  @Mapping(source = "questionType", target = "questionType")
  @Mapping(source = "options", target = "options")
  @Mapping(source = "lesson", target = "lesson", qualifiedByName = "fromEntityToLessonStudentDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToLessonQuestionStudentDto")
  LessonQuestionStudentDto fromEntityToLessonQuestionStudentDto(LessonQuestion lessonQuestion);

  @IterableMapping(elementTargetType = LessonQuestionStudentDto.class, qualifiedByName = "fromEntityToLessonQuestionStudentDto")
  List<LessonQuestionStudentDto> fromEntityToLessonQuestionStudentDtoList(List<LessonQuestion> questions);

  @Mapping(source = "question", target = "question")
  @Mapping(source = "questionType", target = "questionType")
  @Mapping(source = "options", target = "options")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateLessonQuestionFormToEntity(UpdateLessonQuestionForm updateLessonQuestionForm, @MappingTarget LessonQuestion lessonQuestion);
}
