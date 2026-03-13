package com.base.auth.mapper;

import com.base.auth.dto.studentSubmission.StudentSubmissionDisplayDto;
import com.base.auth.model.StudentSubmission;
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
    uses = {TaskQuestionMapper.class})
public interface StudentSubmissionMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "answer", target = "answer")
  @Mapping(source = "taskQuestion", target = "taskQuestion", qualifiedByName = "fromEntityToTaskQuestionStudentDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToStudentSubmissionDisplayDto")
  StudentSubmissionDisplayDto fromEntityToStudentSubmissionDisplayDto(StudentSubmission studentSubmission);

  @IterableMapping(elementTargetType = StudentSubmissionDisplayDto.class, qualifiedByName = "fromEntityToStudentSubmissionDisplayDto")
  List<StudentSubmissionDisplayDto> fromEntityToStudentSubmissionDisplayDtoList(List<StudentSubmission> studentSubmissions);
}
