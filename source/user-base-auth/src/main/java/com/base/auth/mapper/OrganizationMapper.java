package com.base.auth.mapper;

import com.base.auth.dto.organization.OrganizationClientDto;
import com.base.auth.dto.organization.OrganizationDisplayDto;
import com.base.auth.dto.organization.OrganizationDto;
import com.base.auth.form.organization.CreateOrganizationForm;
import com.base.auth.form.organization.UpdateOrganizationForm;
import com.base.auth.model.Organization;
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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizationMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "shortName", target = "shortName")
  @Mapping(source = "logoUrl", target = "logoUrl")
  @Mapping(source = "hotline", target = "hotline")
  @Mapping(source = "type", target = "type")
  @BeanMapping(ignoreByDefault = true)
  Organization fromCreateOrganizationFormToEntity(CreateOrganizationForm createOrganizationForm);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "shortName", target = "shortName")
  @Mapping(source = "hotline", target = "hotline")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "logoUrl", target = "logoUrl")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateOrganizationFormToEntity(UpdateOrganizationForm updateOrganizationForm, @MappingTarget Organization organization);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "shortName", target = "shortName")
  @Mapping(source = "logoUrl", target = "logoUrl")
  @Mapping(source = "hotline", target = "hotline")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToOrganizationDto")
  OrganizationDto fromEntityToOrganizationDto(Organization organization);

  @IterableMapping(elementTargetType = OrganizationDto.class, qualifiedByName = "fromEntityToOrganizationDto")
  List<OrganizationDto> fromEntityToOrganizationDtoList(List<Organization> organizations);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "shortName", target = "shortName")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToOrganizationDisplayDto")
  OrganizationDisplayDto fromEntityToOrganizationDisplayDto(Organization organization);

  @IterableMapping(elementTargetType = OrganizationDisplayDto.class, qualifiedByName = "fromEntityToOrganizationDisplayDto")
  List<OrganizationDisplayDto> fromEntityToOrganizationDisplayDtoList(List<Organization> organizations);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "shortName", target = "shortName")
  @Mapping(source = "logoUrl", target = "logoUrl")
  @Mapping(source = "hotline", target = "hotline")
  @Mapping(source = "type", target = "type")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToOrganizationClientDto")
  OrganizationClientDto fromEntityToOrganizationClientDto(Organization organization);
}
