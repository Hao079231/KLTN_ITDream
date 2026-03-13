package com.base.auth.mapper;

import com.base.auth.dto.reviewSubmission.ReviewSubmissionDisplayDto;
import com.base.auth.model.ReviewSubmission;
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
    uses = {StudentMapper.class, TaskMapper.class})
public interface ReviewSubmissionMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "studentSubmission", target = "studentSubmission", qualifiedByName = "fromEntityToStudentSubmissionDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToReviewSubmissionDisplayDto")
  ReviewSubmissionDisplayDto fromEntityToReviewSubmissionDisplayDto(ReviewSubmission reviewSubmission);

  @IterableMapping(elementTargetType = ReviewSubmissionDisplayDto.class, qualifiedByName = "fromEntityToReviewSubmissionDisplayDto")
  List<ReviewSubmissionDisplayDto> fromEntityToReviewSubmissionDisplayDtoList(List<ReviewSubmission> reviewSubmissions);
}
