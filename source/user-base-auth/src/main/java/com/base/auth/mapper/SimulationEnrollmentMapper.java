package com.base.auth.mapper;

import com.base.auth.dto.simulationEnrollment.SimulationEnrollmentDisplayDto;
import com.base.auth.dto.simulationEnrollment.SimulationEnrollmentDto;
import com.base.auth.dto.simulationEnrollment.StudentLessonViewsDto;
import com.base.auth.model.SimulationEnrollment;
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
    uses = {SimulationMapper.class, StudentMapper.class})
public interface SimulationEnrollmentMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "progress", target = "progress")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromEntityToStudentDto")
  @Mapping(source = "simulation", target = "simulation", qualifiedByName = "fromEntityToSimulationDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToSimulationEnrollmentDto")
  SimulationEnrollmentDto fromEntityToSimulationEnrollmentDto(SimulationEnrollment simulationEnrollment);

  @IterableMapping(elementTargetType = SimulationEnrollmentDto.class, qualifiedByName = "fromEntityToSimulationEnrollmentDto")
  List<SimulationEnrollmentDto> fromEntityToSimulationEnrollmentDtoList(List<SimulationEnrollment> simulationEnrollment);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "progress", target = "progress")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToProfileDto")
  @Mapping(source = "simulation", target = "simulation", qualifiedByName = "fromEntityToSimulationDisplayDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToSimulationEnrollmentDisplayDto")
  SimulationEnrollmentDisplayDto fromEntityToSimulationEnrollmentDisplayDto(SimulationEnrollment simulationEnrollment);

  @IterableMapping(elementTargetType = SimulationEnrollmentDisplayDto.class, qualifiedByName = "fromEntityToSimulationEnrollmentDisplayDto")
  List<SimulationEnrollmentDisplayDto> fromEntityToSimulationEnrollmentDisplayDtoList(List<SimulationEnrollment> simulationEnrollment);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "simulation", target = "simulation", qualifiedByName = "fromEntityToSimulationDisplayDto")
  @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToProfileDto")
  @Mapping(source = "reviewStatus", target = "reviewStatus")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToStudentLessonViewsDto")
  StudentLessonViewsDto fromEntityToStudentLessonViewsDto(SimulationEnrollment simulationEnrollment);

  @IterableMapping(elementTargetType = StudentLessonViewsDto.class, qualifiedByName = "fromEntityToStudentLessonViewsDto")
  List<StudentLessonViewsDto> fromEntityToStudentLessonViewsDtoList(List<SimulationEnrollment> simulationEnrollment);

}
