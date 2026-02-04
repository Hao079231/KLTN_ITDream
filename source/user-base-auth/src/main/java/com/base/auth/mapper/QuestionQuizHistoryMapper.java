package com.base.auth.mapper;

import com.base.auth.form.questionQuizHistory.CreateQuestionQuizHistoryForm;
import com.base.auth.model.QuestionQuizHistory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionQuizHistoryMapper {
  @Mapping(source = "answer", target = "answer")
  @Mapping(source = "isCorrect", target = "isCorrect")
  @BeanMapping(ignoreByDefault = true)
  QuestionQuizHistory fromCreateQuestionQuizHistoryFormToEntity(CreateQuestionQuizHistoryForm createQuestionQuizHistoryForm);
}
