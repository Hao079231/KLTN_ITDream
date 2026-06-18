package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.organization.OrganizationDisplayDto;
import com.base.auth.dto.organization.OrganizationDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.organization.CreateOrganizationForm;
import com.base.auth.form.organization.UpdateOrganizationForm;
import com.base.auth.mapper.OrganizationMapper;
import com.base.auth.model.Educator;
import com.base.auth.model.Organization;
import com.base.auth.model.Simulation;
import com.base.auth.model.criteria.OrganizationCriteria;
import com.base.auth.repository.EducatorRepository;
import com.base.auth.repository.OrganizationRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.service.SimulationService;
import com.base.auth.service.UserBaseApiService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/organization")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class OrganizationController extends ABasicController{
  @Autowired
  OrganizationRepository organizationRepository;

  @Autowired
  OrganizationMapper organizationMapper;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  SimulationService simulationService;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('O_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateOrganizationForm form, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existOrganization = organizationRepository.existsByNameAndShortName(form.getName(), form.getShortName());
    if (existOrganization){
      throw new BadRequestException("Tổ chức đã tồn tại", ErrorCode.ORGANIZATION_ERROR_EXIST);
    }
    Organization organization = organizationMapper.fromCreateOrganizationFormToEntity(form);
    organizationRepository.save(organization);
    apiMessageDto.setMessage("Tạo tổ chức thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('O_L')")
  public ApiMessageDto<ResponseListDto<List<OrganizationDto>>> list(OrganizationCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<OrganizationDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<OrganizationDto>> responseListDto = new ResponseListDto<>();
    Page<Organization> organizations = organizationRepository.findAll(criteria.getSpecification(), pageable);
    List<OrganizationDto> organizationDtos = organizationMapper.fromEntityToOrganizationDtoList(organizations.getContent());
    responseListDto.setContent(organizationDtos);
    responseListDto.setTotalElements(organizations.getTotalElements());
    responseListDto.setTotalPages(organizations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách tổ chức thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<OrganizationDisplayDto>>> listDisplay(OrganizationCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<OrganizationDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<OrganizationDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Organization> organizations = organizationRepository.findAll(criteria.getSpecification(), pageable);
    List<OrganizationDisplayDto> organizationDtos = organizationMapper.fromEntityToOrganizationDisplayDtoList(organizations.getContent());
    responseListDto.setContent(organizationDtos);
    responseListDto.setTotalElements(organizations.getTotalElements());
    responseListDto.setTotalPages(organizations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách tổ chức thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('O_V')")
  public ApiMessageDto<OrganizationDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<OrganizationDto> apiMessageDto = new ApiMessageDto<>();
    Organization organization = organizationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tổ chức", ErrorCode.ORGANIZATION_ERROR_NOT_FOUND));
    OrganizationDto organizationDto = organizationMapper.fromEntityToOrganizationDto(organization);
    apiMessageDto.setData(organizationDto);
    apiMessageDto.setMessage("Lấy chi tiết tổ chức thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('O_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateOrganizationForm form, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Organization organization = organizationRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tổ chức", ErrorCode.ORGANIZATION_ERROR_NOT_FOUND));
    if (!organization.getName().equals(form.getName()) || !organization.getShortName().equals(form.getShortName())){
      Boolean existOrganization = organizationRepository.existsByNameAndShortName(form.getName(), form.getShortName());
      if (existOrganization){
        throw new BadRequestException("Tổ chức đã tồn tại", ErrorCode.ORGANIZATION_ERROR_EXIST);
      }
    }
    organizationMapper.fromUpdateOrganizationFormToEntity(form, organization);
    if (!organization.getLogoUrl().equals(form.getLogoUrl()) && !form.getLogoUrl().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(organization.getLogoUrl());
    }
    organizationRepository.save(organization);
    apiMessageDto.setMessage("Cập nhật tổ chức thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('O_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Organization organization = organizationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tổ chức", ErrorCode.ORGANIZATION_ERROR_NOT_FOUND));
    List<Educator> educators = educatorRepository.findAllByOrganizationId(id);
    for (Educator educator : educators){
      List<Simulation> simulations = simulationRepository.findAllByEducatorId(educator.getId());
      for (Simulation simulation : simulations){
        simulationService.deleteAllBySimulation(simulation);
      }
      userBaseApiService.deleteByFilePath(educator.getAccount().getAvatarPath());
    }
    userBaseApiService.deleteByFilePath(organization.getLogoUrl());
    organizationRepository.delete(organization);
    apiMessageDto.setMessage("Xóa tổ chức thành công");
    return apiMessageDto;
  }
}