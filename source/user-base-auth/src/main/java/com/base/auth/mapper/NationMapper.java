package com.base.auth.mapper;

import com.base.auth.dto.nation.NationAdminDto;
import com.base.auth.dto.nation.NationDto;
import com.base.auth.form.nation.CreateNationForm;
import com.base.auth.form.nation.UpdateNationForm;
import com.base.auth.model.Nation;
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
public interface NationMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @BeanMapping(ignoreByDefault = true)
  Nation fromCreateNationFormToEntity(CreateNationForm createNationForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToNationDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToNationDto")
  NationDto fromEntityToNationDto(Nation nation);

  @IterableMapping(elementTargetType = NationDto.class, qualifiedByName = "fromEntityToNationDto")
  List<NationDto> fromEntityToNationDtoList(List<Nation> nations);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToNationDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToNationAdminDto")
  NationAdminDto fromEntityToNationAdminDto(Nation nation);

  @IterableMapping(elementTargetType = NationAdminDto.class, qualifiedByName = "fromEntityToNationAdminDto")
  List<NationAdminDto> fromEntityToNationAdminDtoList(List<Nation> nations);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateNationFormToEntity(UpdateNationForm updateNationForm, @MappingTarget Nation nation);
}
