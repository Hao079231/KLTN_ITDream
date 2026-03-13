package com.base.auth.mapper;

import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDisplayDto;
import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDto;
import com.base.auth.model.StudentTaskProgress;
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
    uses = {SimulationEnrollmentMapper.class, TaskMapper.class})
public interface StudentTaskProgressMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "errorCount", target = "errorCount")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "task", target = "task", qualifiedByName = "fromEntityToTaskDto")
  @Mapping(source = "simulationEnrollment", target = "simulationEnrollment", qualifiedByName = "fromEntityToSimulationEnrollmentDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToStudentTaskProgressDto")
  StudentTaskProgressDto fromEntityToStudentTaskProgressDto(StudentTaskProgress studentTaskProgress);

  @IterableMapping(elementTargetType = StudentTaskProgressDto.class, qualifiedByName = "fromEntityToStudentTaskProgressDto")
  List<StudentTaskProgressDto> fromEntityToStudentTaskProgressDtoList(List<StudentTaskProgress> studentTaskProgressList);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "errorCount", target = "errorCount")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "task", target = "task", qualifiedByName = "fromEntityToTaskDisplayDto")
  @Mapping(source = "simulationEnrollment", target = "simulationEnrollment", qualifiedByName = "fromEntityToSimulationEnrollmentDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToStudentTaskProgressDisplayDto")
  StudentTaskProgressDisplayDto fromEntityToStudentTaskProgressDisplayDto(StudentTaskProgress studentTaskProgress);

  @IterableMapping(elementTargetType = StudentTaskProgressDisplayDto.class, qualifiedByName = "fromEntityToStudentTaskProgressDisplayDto")
  List<StudentTaskProgressDisplayDto> fromEntityToStudentTaskProgressDisplayDtoList(List<StudentTaskProgress> studentTaskProgressList);
}
