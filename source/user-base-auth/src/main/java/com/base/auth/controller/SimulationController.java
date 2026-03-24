package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.simulation.SimulationDisplayDto;
import com.base.auth.dto.simulation.SimulationClientDto;
import com.base.auth.dto.simulation.SimulationDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.simulation.CreateSimulationForm;
import com.base.auth.form.simulation.RequestSimulationIdForm;
import com.base.auth.form.simulation.UpdateSimulationForm;
import com.base.auth.form.RequestProcessVideoMessageForm;
import com.base.auth.mapper.SimulationMapper;
import com.base.auth.model.Simulation;
import com.base.auth.model.Educator;
import com.base.auth.model.Category;
import com.base.auth.model.criteria.SimulationCriteria;
import com.base.auth.repository.EducatorRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.service.SimulationService;
import com.base.auth.service.ProcessVideoService;
import com.base.auth.service.UserBaseApiService;
import java.io.File;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/v1/simulation")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SimulationController extends ABasicController{
  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  SimulationMapper simulationMapper;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  EducatorRepository educatorRepository;

  @Autowired
  ProcessVideoService processVideoService;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Autowired
  SimulationService simulationService;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateSimulationForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(getCurrentUser()).orElseThrow(()
        -> new NotFoundException("Educator not found"));
    if (!isEducator()){
      throw new BadRequestException("User is not an educator", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(()
    -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    boolean existSimulation = simulationRepository.existsByTitleAndEducatorId(form.getTitle(), getCurrentUser());
    if (existSimulation){
      throw new BadRequestException("Simulation already exist", ErrorCode.SIMULATION_ERROR_EXIST);
    }
    Simulation simulation = simulationMapper.fromCreateSimulationFormToEntity(form);
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulation.setCategory(category);
    simulation.setEducator(educator);
    if (!form.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_PROCESSING);
    } else {
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_DONE);
    }
    simulationRepository.saveAndFlush(simulation);

    if (!simulation.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(simulation.getId());
      data.setKind(ITDreamConstant.KIND_SIMULATION);
      data.setUrl(form.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setMessage("Create simulation success. Please wait for approval");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_L')")
  public ApiMessageDto<ResponseListDto<List<SimulationDto>>> getList(
      SimulationCriteria simulationCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<SimulationDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<SimulationDto>> responseListDto = new ResponseListDto<>();
    Page<Simulation> simulations = simulationRepository.findAll(simulationCriteria.getSpecification(), pageable);
    responseListDto.setContent(simulationMapper.fromEntityToSimulationDtoList(simulations.getContent()));
    responseListDto.setTotalElements(simulations.getTotalElements());
    responseListDto.setTotalPages(simulations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_V')")
  public ApiMessageDto<SimulationDto> get(@PathVariable("id") Long id){
    ApiMessageDto<SimulationDto> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(() ->
        new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    SimulationDto simulationDto = simulationMapper.fromEntityToSimulationDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Get simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<SimulationDisplayDto>>> getListForClient(SimulationCriteria criteria,  Pageable pageable){
    ApiMessageDto<ResponseListDto<List<SimulationDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<SimulationDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.SIMULATION_STATUS_ACTIVE);
    Page<Simulation> simulations = simulationRepository.findAll(criteria.getSpecification(), pageable);
    List<SimulationDisplayDto> simulationDtos = simulationMapper.fromEntityToSimulationDisplayDtoList(simulations.getContent());
    responseListDto.setContent(simulationDtos);
    responseListDto.setTotalElements(simulations.getTotalElements());
    responseListDto.setTotalPages(simulations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ST_L')")
  public ApiMessageDto<ResponseListDto<List<SimulationDisplayDto>>> getListForStudent(SimulationCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<SimulationDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<SimulationDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.SIMULATION_STATUS_ACTIVE);
    Page<Simulation> simulations = simulationRepository.findAll(criteria.getSpecification(), pageable);
    List<SimulationDisplayDto> simulationDtos = simulationMapper.fromEntityToSimulationDisplayDtoList(simulations.getContent());
    responseListDto.setContent(simulationDtos);
    responseListDto.setTotalElements(simulations.getTotalElements());
    responseListDto.setTotalPages(simulations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_L')")
  public ApiMessageDto<ResponseListDto<List<SimulationDisplayDto>>> getListForEducator(SimulationCriteria simulationCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<SimulationDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<SimulationDisplayDto>> responseListDto = new ResponseListDto<>();
    simulationCriteria.setEducatorId(getCurrentUser());
    Page<Simulation> simulations = simulationRepository.findAll(simulationCriteria.getSpecification(), pageable);
    responseListDto.setContent(simulationMapper.fromEntityToSimulationDisplayDtoList(simulations.getContent()));
    responseListDto.setTotalElements(simulations.getTotalElements());
    responseListDto.setTotalPages(simulations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<SimulationClientDto> getSimulationForClient(@PathVariable("id") Long id){
    ApiMessageDto<SimulationClientDto> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    SimulationClientDto simulationDto = simulationMapper.fromEntityToSimulationClientDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Get simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ST_V')")
  public ApiMessageDto<SimulationClientDto> getSimulationForStudent(@PathVariable("id") Long id){
    ApiMessageDto<SimulationClientDto> apiMessageDto = new ApiMessageDto<>();
    if (!isStudent()){
      throw new BadRequestException("User is not a student", ErrorCode.USER_ERROR_NOT_STUDENT);
    }
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    SimulationClientDto simulationDto = simulationMapper.fromEntityToSimulationClientDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Get simulation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_V')")
  public ApiMessageDto<SimulationClientDto> getSimulationForEducator(@PathVariable("id") Long id){
    ApiMessageDto<SimulationClientDto> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("User is not an educator", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(simulation.getEducator().getId(), getCurrentUser())){
      throw new BadRequestException("Simulation cannot be read", ErrorCode.SIMULATION_ERROR_NOT_AUTHORIZED);
    }
    SimulationClientDto simulationDto = simulationMapper.fromEntityToSimulationClientDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Get simulation success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateSimulationForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("User is not an educator");
    }
    Simulation simulation = simulationRepository.findById(form.getId()).orElseThrow(()
    -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(simulation.getEducator().getId(), getCurrentUser())){
      throw new BadRequestException("Simulation cannot be updated", ErrorCode.SIMULATION_ERROR_NOT_AUTHORIZED);
    }
    if (form.getCategoryId() != null && !Objects.equals(simulation.getCategory().getId(), form.getCategoryId())){
      Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(()
          -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      simulation.setCategory(category);
    }

    if (!simulation.getThumbnail().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN) &&
        !Objects.equals(form.getThumbnail(), simulation.getThumbnail())){
      userBaseApiService.deleteByFilePath(simulation.getThumbnail());
    }

    if (!simulation.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN) &&
        !Objects.equals(form.getVideoPath(), simulation.getVideoPath())){
      userBaseApiService.deleteByFilePath(simulation.getVideoPath());
    }

    simulationMapper.fromUpdateSimulationFormToEntity(form, simulation);
    if (!form.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_PROCESSING);
    } else {
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_DONE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.saveAndFlush(simulation);

    if (!simulation.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(simulation.getId());
      data.setKind(ITDreamConstant.KIND_SIMULATION);
      data.setUrl(form.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setMessage("Update success. Please wait for approval");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/approve_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_APD')")
  public ApiMessageDto<String> approveDelete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));

    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE_DELETE, simulation.getStatus())) {
      throw new BadRequestException("Simulation cannot be deleted", ErrorCode.SIMULATION_ERROR_NOT_DELETE);
    }

    simulationService.deleteFileSimulation(simulation);
    simulationService.deleteAllBySimulation(simulation);
    apiMessageDto.setMessage("Approve delete simulation success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/reject_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_RJD')")
  public ApiMessageDto<String> rejectDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE_DELETE, simulation.getStatus())){
      throw new BadRequestException("Simulation cannot be deleted", ErrorCode.SIMULATION_ERROR_NOT_DELETE);
    }
    simulation.setStatus(ITDreamConstant.STATUS_ACTIVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Reject delete simulation success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/educator_request_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_RED')")
  public ApiMessageDto<String> requestDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("User is not an educator", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.STATUS_ACTIVE, simulation.getStatus())){
      throw new BadRequestException("Request for deletion is currently being approved", ErrorCode.SIMULATION_ERROR_APPROVE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE_DELETE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Request delete simulation success");
    return apiMessageDto;
  }

  @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_AP')")
  public ApiMessageDto<String> approve(@Valid @RequestBody RequestSimulationIdForm requestSimulationIdForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(requestSimulationIdForm.getId()).orElseThrow(()
    -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE, simulation.getStatus())){
      throw new BadRequestException("Simulation cannot approve", ErrorCode.SIMULATION_ERROR_APPROVE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_ACTIVE);
    simulation.setNotice(null);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Approve simulation success");
    return apiMessageDto;
  }

  @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_RJ')")
  public ApiMessageDto<String> reject(@Valid @RequestBody RequestSimulationIdForm requestSimulationIdForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(requestSimulationIdForm.getId()).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE, simulation.getStatus())){
      throw new BadRequestException("Simulation cannot approve", ErrorCode.SIMULATION_ERROR_APPROVE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_REJECT);
    simulation.setNotice(requestSimulationIdForm.getNotice());
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Reject simulation success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/educator_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_D')")
  public ApiMessageDto<String> deleteByEducator(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE, simulation.getStatus())){
      throw new BadRequestException("Simulation cannot be deleted", ErrorCode.SIMULATION_ERROR_NOT_DELETE);
    }
    simulationService.deleteFileSimulation(simulation);
    simulationService.deleteAllBySimulation(simulation);
    apiMessageDto.setMessage("Delete simulation success");
    return apiMessageDto;
  }
}
