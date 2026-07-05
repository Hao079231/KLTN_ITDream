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
import com.base.auth.form.simulation.UpdateSimulationStatusForm;
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
        -> new NotFoundException("Không tìm thấy giảng viên"));
    if (!isEducator()){
      throw new BadRequestException("Người dùng không phải là giảng viên", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy danh mục", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    boolean existSimulation = simulationRepository.existsByTitleAndEducatorId(form.getTitle(), getCurrentUser());
    if (existSimulation){
      throw new BadRequestException("Mô phỏng đã tồn tại", ErrorCode.SIMULATION_ERROR_EXIST);
    }
    Simulation simulation = simulationMapper.fromCreateSimulationFormToEntity(form);
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulation.setCategory(category);
    simulation.setEducator(educator);
    if (StringUtils.isNotBlank(form.getVideoPath())
        && !form.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_PROCESSING);
    } else {
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_DONE);
    }
    simulationRepository.saveAndFlush(simulation);

    if (StringUtils.isNotBlank(form.getVideoPath())
        && !simulation.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(simulation.getId());
      data.setKind(ITDreamConstant.KIND_SIMULATION);
      data.setUrl(form.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setMessage("Tạo mô phỏng thành công. Vui lòng chờ phê duyệt");
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
    apiMessageDto.setMessage("Lấy danh sách mô phỏng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_V')")
  public ApiMessageDto<SimulationDto> get(@PathVariable("id") Long id){
    ApiMessageDto<SimulationDto> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(() ->
        new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    SimulationDto simulationDto = simulationMapper.fromEntityToSimulationDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Lấy mô phỏng thành công");
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
    apiMessageDto.setMessage("Lấy danh sách mô phỏng thành công");
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
    apiMessageDto.setMessage("Lấy danh sách mô phỏng thành công");
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
    apiMessageDto.setMessage("Lấy danh sách mô phỏng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<SimulationClientDto> getSimulationForClient(@PathVariable("id") Long id){
    ApiMessageDto<SimulationClientDto> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    SimulationClientDto simulationDto = simulationMapper.fromEntityToSimulationClientDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Lấy mô phỏng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ST_V')")
  public ApiMessageDto<SimulationClientDto> getSimulationForStudent(@PathVariable("id") Long id){
    ApiMessageDto<SimulationClientDto> apiMessageDto = new ApiMessageDto<>();
    if (!isStudent()){
      throw new BadRequestException("Người dùng không phải là học viên", ErrorCode.USER_ERROR_NOT_STUDENT);
    }
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    SimulationClientDto simulationDto = simulationMapper.fromEntityToSimulationClientDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Lấy mô phỏng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_V')")
  public ApiMessageDto<SimulationClientDto> getSimulationForEducator(@PathVariable("id") Long id){
    ApiMessageDto<SimulationClientDto> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("Người dùng không phải là giảng viên", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(simulation.getEducator().getId(), getCurrentUser())){
      throw new BadRequestException("Không được phép xem mô phỏng này", ErrorCode.SIMULATION_ERROR_NOT_AUTHORIZED);
    }
    SimulationClientDto simulationDto = simulationMapper.fromEntityToSimulationClientDto(simulation);
    apiMessageDto.setData(simulationDto);
    apiMessageDto.setMessage("Lấy mô phỏng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateSimulationForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("Người dùng không phải là giảng viên");
    }
    Simulation simulation = simulationRepository.findById(form.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (simulation.getEducator() != null){
      if (!Objects.equals(simulation.getEducator().getId(), getCurrentUser())){
        throw new BadRequestException("Không được phép cập nhật mô phỏng này", ErrorCode.SIMULATION_ERROR_NOT_AUTHORIZED);
      }
    }

    if (form.getCategoryId() != null
        && (simulation.getCategory() == null
        || !Objects.equals(simulation.getCategory().getId(), form.getCategoryId()))){
      Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow(()
          -> new NotFoundException("Không tìm thấy danh mục", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      simulation.setCategory(category);
    }

    if (StringUtils.isNotBlank(simulation.getThumbnail())
        && StringUtils.isNotBlank(form.getThumbnail())
        && !simulation.getThumbnail().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)
        && !Objects.equals(form.getThumbnail(), simulation.getThumbnail())){
      userBaseApiService.deleteByFilePath(simulation.getThumbnail());
    }

    if (StringUtils.isNotBlank(simulation.getVideoPath())
        && StringUtils.isNotBlank(form.getVideoPath())
        && !simulation.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)
        && !Objects.equals(form.getVideoPath(), simulation.getVideoPath())){
      userBaseApiService.deleteByFilePath(simulation.getVideoPath());
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_PROCESSING);
    }

    simulationMapper.fromUpdateSimulationFormToEntity(form, simulation);
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    if (StringUtils.isNotBlank(form.getVideoPath())
        && form.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      simulation.setVideoState(ITDreamConstant.STATE_SIMULATION_DONE);
    }
    simulationRepository.save(simulation);

    if (StringUtils.isNotBlank(simulation.getVideoPath())
        && !simulation.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(simulation.getId());
      data.setKind(ITDreamConstant.KIND_SIMULATION);
      data.setUrl(form.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }

    apiMessageDto.setMessage("Cập nhật thành công. Vui lòng chờ phê duyệt");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/approve_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_APD')")
  public ApiMessageDto<String> approveDelete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));

    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE_DELETE, simulation.getStatus())) {
      throw new BadRequestException("Không thể xóa mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_DELETE);
    }

    simulationService.deleteFileSimulation(simulation);
    simulationService.deleteAllBySimulation(simulation);
    apiMessageDto.setMessage("Duyệt yêu cầu xóa mô phỏng thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/reject_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_RJD')")
  public ApiMessageDto<String> rejectDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE_DELETE, simulation.getStatus())){
      throw new BadRequestException("Không thể xóa mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_DELETE);
    }
    simulation.setStatus(ITDreamConstant.STATUS_ACTIVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Từ chối yêu cầu xóa mô phỏng thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/educator_request_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_RED')")
  public ApiMessageDto<String> requestDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("Người dùng không phải là giảng viên", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.STATUS_ACTIVE, simulation.getStatus())){
      throw new BadRequestException("Yêu cầu xóa đang trong quá trình phê duyệt", ErrorCode.SIMULATION_ERROR_APPROVE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE_DELETE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Yêu cầu xóa mô phỏng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_AP')")
  public ApiMessageDto<String> approve(@Valid @RequestBody RequestSimulationIdForm requestSimulationIdForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(requestSimulationIdForm.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE, simulation.getStatus())){
      throw new BadRequestException("Không thể duyệt mô phỏng", ErrorCode.SIMULATION_ERROR_APPROVE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_ACTIVE);
    simulation.setNotice(null);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Duyệt mô phỏng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_RJ')")
  public ApiMessageDto<String> reject(@Valid @RequestBody RequestSimulationIdForm requestSimulationIdForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(requestSimulationIdForm.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE, simulation.getStatus())){
      throw new BadRequestException("Không thể duyệt mô phỏng", ErrorCode.SIMULATION_ERROR_APPROVE);
    }
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_REJECT);
    simulation.setNotice(requestSimulationIdForm.getNotice());
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Từ chối mô phỏng thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/educator_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_ED_D')")
  public ApiMessageDto<String> deleteByEducator(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE, simulation.getStatus())){
      throw new BadRequestException("Không thể xóa mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_DELETE);
    }
    simulationService.deleteFileSimulation(simulation);
    simulationService.deleteAllBySimulation(simulation);
    apiMessageDto.setMessage("Xóa mô phỏng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update-status", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SI_UST') or hasRole('SI_ED_U')")
  public ApiMessageDto<String> updateStatus(@Valid @RequestBody UpdateSimulationStatusForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Mô phỏng không tồn tại", ErrorCode.SIMULATION_ERROR_NOT_FOUND));

    if (isAdmin()){
      simulation.setStatus(form.getStatus());
      simulation.setNotice(form.getNotice());
    } else if (isEducator()){
      if (simulation.getEducator() == null || !Objects.equals(simulation.getEducator().getId(), getCurrentUser())){
        throw new UnauthorizationException("Không được phép cập nhật trạng thái mô phỏng này");
      }
      if (form.getStatus() != ITDreamConstant.SIMULATION_STATUS_ACTIVE && form.getStatus() != 0){
        throw new BadRequestException("Trạng thái không hợp lệ dành cho giảng viên");
      }
      if (simulation.getStatus() != ITDreamConstant.SIMULATION_STATUS_ACTIVE && simulation.getStatus() != 0){
        throw new BadRequestException("Không thể thay đổi trạng thái hiện tại của mô phỏng này");
      }
      simulation.setStatus(form.getStatus());
      simulation.setNotice(null);
    } else {
      throw new UnauthorizationException("Người dùng không có quyền thực hiện thao tác này");
    }
    
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Cập nhật trạng thái mô phỏng thành công");
    return apiMessageDto;
  }
}