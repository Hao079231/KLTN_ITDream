package com.base.auth.mapper;

import com.base.auth.dto.feedback.FeedbackClientDto;
import com.base.auth.dto.feedback.FeedbackDto;
import com.base.auth.form.feedback.CreateFeedbackForm;
import com.base.auth.form.feedback.UpdateFeedbackForm;
import com.base.auth.model.Feedback;
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
    uses = {StudentMapper.class, SimulationMapper.class})
public interface FeedbackMapper {
  @Mapping(source = "star", target = "star")
  @Mapping(source = "content", target = "content")
  @BeanMapping(ignoreByDefault = true)
  Feedback fromCreateFeedbackFormToEntity(CreateFeedbackForm createFeedbackForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "star", target = "star")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromEntityToStudentDto")
  @Mapping(source = "simulation", target = "simulation", qualifiedByName = "fromEntityToSimulationDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToFeedbackDto")
  FeedbackDto fromEntityToFeedbackDto(Feedback feedback);

  @IterableMapping(elementTargetType = FeedbackDto.class, qualifiedByName = "fromEntityToFeedbackDto")
  List<FeedbackDto> fromEntityToFeedbackDtoList(List<Feedback> feedbacks);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "star", target = "star")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToProfileDto")
  @Mapping(source = "simulation", target = "simulation", qualifiedByName = "fromEntityToSimulationDisplayDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToFeedbackClientDto")
  FeedbackClientDto fromEntityToFeedbackClientDto(Feedback feedback);

  @IterableMapping(elementTargetType = FeedbackClientDto.class, qualifiedByName = "fromEntityToFeedbackClientDto")
  List<FeedbackClientDto> fromEntityToFeedbackClientDtoList(List<Feedback> feedbacks);

  @Mapping(source = "star", target = "star")
  @Mapping(source = "content", target = "content")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateFeedbackFormToEntity(UpdateFeedbackForm updateFeedbackForm, @MappingTarget Feedback feedback);
}
