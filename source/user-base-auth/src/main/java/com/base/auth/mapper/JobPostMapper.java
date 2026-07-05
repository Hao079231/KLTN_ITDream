package com.base.auth.mapper;

import com.base.auth.dto.jobPost.JobPostAdminDto;
import com.base.auth.dto.jobPost.JobPostDto;
import com.base.auth.form.jobPost.CreateJobPostForm;
import com.base.auth.form.jobPost.UpdateJobPostForm;
import com.base.auth.model.JobPost;
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
    uses = {SimulationMapper.class, NationMapper.class})
public interface JobPostMapper {
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "jobUrl", target = "jobUrl")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "roleType", target = "roleType")
  @Mapping(source = "address", target = "address")
  @BeanMapping(ignoreByDefault = true)
  JobPost fromCreateJobPostFormToEntity(CreateJobPostForm createJobPostForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "jobUrl", target = "jobUrl")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "roleType", target = "roleType")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToNationAdminDto")
  @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToNationAdminDto")
  @Mapping(source = "date", target = "date")
  @Mapping(source = "endDate", target = "endDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEntityToEducatorDto")
  @Mapping(source = "simulations", target = "simulations", qualifiedByName = "fromEntityToSimulationDtoList")
  @Mapping(source = "notice", target = "notice")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToJobPostAdminDto")
  JobPostAdminDto fromEntityToJobPostAdminDto(JobPost jobPost);

  @IterableMapping(elementTargetType = JobPostAdminDto.class, qualifiedByName = "fromEntityToJobPostAdminDto")
  List<JobPostAdminDto> fromEntityToJobPostAdminDtoList(List<JobPost> jobPosts);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "jobUrl", target = "jobUrl")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "roleType", target = "roleType")
  @Mapping(source = "address", target = "address")
  @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToNationDto")
  @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToNationDto")
  @Mapping(source = "date", target = "date")
  @Mapping(source = "endDate", target = "endDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "educator", target = "educator", qualifiedByName = "fromEducatorToProfileDto")
  @Mapping(source = "simulations", target = "simulations", qualifiedByName = "fromEntityToSimulationDisplayDtoList")
  @Mapping(source = "notice", target = "notice")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToJobPostDto")
  JobPostDto fromEntityToJobPostDto(JobPost jobPost);

  @IterableMapping(elementTargetType = JobPostDto.class, qualifiedByName = "fromEntityToJobPostDto")
  List<JobPostDto> fromEntityToJobPostDtoList(List<JobPost> jobPosts);

  @Mapping(source = "title", target = "title")
  @Mapping(source = "content", target = "content")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "jobUrl", target = "jobUrl")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "roleType", target = "roleType")
  @Mapping(source = "address", target = "address")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateJobPostFormToEntity(UpdateJobPostForm updateJobPostForm, @MappingTarget JobPost jobPost);
}
