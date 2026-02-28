package com.base.auth.mapper;

import com.base.auth.dto.correctAnswer.CorrectAnswerDisplayDto;
import com.base.auth.model.CorrectAnswer;
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
    uses = {LessonQuestionMapper.class})
public interface CorrectAnswerMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answer", target = "answer")
  @Mapping(source = "lessonQuestion", target = "lessonQuestion")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCorrectAnswerDisplayDto")
  CorrectAnswerDisplayDto fromEntityToCorrectAnswerDisplayDto(CorrectAnswer correctAnswer);

  @IterableMapping(elementTargetType = CorrectAnswerDisplayDto.class, qualifiedByName = "fromEntityToCorrectAnswerDisplayDto")
  List<CorrectAnswerDisplayDto> fromEntityToCorrectAnswerDisplayDtoList(List<CorrectAnswer> correctAnswers);
}
