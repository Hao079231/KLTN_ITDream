package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.task.TaskDisplayDto;
import com.base.auth.dto.task.TaskDto;
import com.base.auth.dto.task.TaskEducatorDto;
import com.base.auth.dto.task.TaskStudentDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.RequestProcessVideoMessageForm;
import com.base.auth.form.task.CreateTaskForm;
import com.base.auth.form.task.UpdateTaskForm;
import com.base.auth.form.task.UpdateTaskPositionForm;
import com.base.auth.mapper.TaskMapper;
import com.base.auth.model.Simulation;
import com.base.auth.model.Task;
import com.base.auth.model.criteria.TaskCriteria;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.TaskRepository;
import com.base.auth.service.TaskService;
import com.base.auth.service.ProcessVideoService;
import com.base.auth.service.UserBaseApiService;
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
@RequestMapping("/v1/task")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class TaskController extends ABasicController{
  @Autowired
  TaskRepository taskRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  TaskMapper taskMapper;

  @Autowired
  ProcessVideoService processVideoService;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Autowired
  TaskService taskService;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_C')")
  public ApiMessageDto<Long> create(@Valid @RequestBody CreateTaskForm form, BindingResult bindingResult){
    if(!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<Long> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(form.getSimulationId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (form.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      Boolean existSubtask = taskRepository.existsBySimulationIdAndNameAndTitle(form.getSimulationId(), form.getName(), form.getTitle());
      if (existSubtask){
        throw new BadRequestException("Tiêu đề nhiệm vụ phụ đã tồn tại", ErrorCode.TASK_ERROR_EXIST);
      }
    }

    Task task = taskMapper.fromCreateTaskFormToEntity(form);
    task.setSimulation(simulation);
    if (form.getKind().equals(ITDreamConstant.TASK_KIND_TASK)) {
      Integer order = taskService.generateOrderInParent(simulation.getId(), null, form.getKind());

      task.setOrderInParent(order);
    }

    if (form.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)) {
      if (form.getParentId() == null){
        throw new NotFoundException("Nhiệm vụ không được để trống", ErrorCode.TASK_ERROR_NOT_FOUND);
      }

      Task parent = taskRepository.findById(form.getParentId())
          .orElseThrow(() -> new NotFoundException("Không tìm thấy nhiệm vụ cha", ErrorCode.TASK_ERROR_NOT_FOUND));
      task.setParent(parent);
      Integer order = taskService.generateOrderInParent(simulation.getId(), parent.getId(), form.getKind());
      task.setOrderInParent(order);
    }

    if (StringUtils.isNotBlank(form.getVideoPath())
        && !form.getVideoPath().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      task.setVideoState(ITDreamConstant.STATE_TASK_PROCESSING);
    } else {
      task.setVideoState(ITDreamConstant.STATE_TASK_DONE);
    }
    taskRepository.save(task);

    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);

    if (StringUtils.isNotBlank(task.getVideoPath())
        && !task.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(task.getId());
      data.setUrl(task.getVideoPath());
      data.setKind(ITDreamConstant.KIND_TASK);
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setData(task.getId());
    apiMessageDto.setMessage("Tạo nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_L')")
  public ApiMessageDto<ResponseListDto<List<TaskDto>>> list(TaskCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskDto>> responseListDto = new ResponseListDto<>();
    Page<Task> tasks = taskRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(taskMapper.fromEntityToTaskDtoList(tasks.getContent()));
    responseListDto.setTotalElements(tasks.getTotalElements());
    responseListDto.setTotalPages(tasks.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_V')")
  public ApiMessageDto<TaskDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<TaskDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    apiMessageDto.setData(taskMapper.fromEntityToTaskDto(task));
    apiMessageDto.setMessage("Lấy nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_L')")
  public ApiMessageDto<ResponseListDto<List<TaskDisplayDto>>> listByEducator(TaskCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Task> tasks = taskRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(taskMapper.fromEntityToTaskDisplayDtoList(tasks.getContent()));
    responseListDto.setTotalElements(tasks.getTotalElements());
    responseListDto.setTotalPages(tasks.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_V')")
  public ApiMessageDto<TaskEducatorDto> getByEducator(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<TaskEducatorDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    apiMessageDto.setData(taskMapper.fromEntityToTaskEducatorDto(task));
    apiMessageDto.setMessage("Lấy nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ST_L')")
  public ApiMessageDto<ResponseListDto<List<TaskDisplayDto>>> listByStudent(TaskCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Task> tasks = taskRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(taskMapper.fromEntityToTaskDisplayDtoList(tasks.getContent()));
    responseListDto.setTotalElements(tasks.getTotalElements());
    responseListDto.setTotalPages(tasks.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<TaskDisplayDto>>> listByGuest(TaskCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setKind(ITDreamConstant.TASK_KIND_TASK);
    Page<Task> tasks = taskRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(taskMapper.fromEntityToTaskDisplayDtoList(tasks.getContent()));
    responseListDto.setTotalElements(tasks.getTotalElements());
    responseListDto.setTotalPages(tasks.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách nhiệm vụ thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ST_V')")
  public ApiMessageDto<TaskStudentDto> getByStudent(@PathVariable("id") Long id){
    if (!isStudent()){
      throw new UnauthorizationException("Người dùng không phải là học viên");
    }
    ApiMessageDto<TaskStudentDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    apiMessageDto.setData(taskMapper.fromEntityToTaskStudentDto(task));
    apiMessageDto.setMessage("Lấy nhiệm vụ thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateTaskForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    if (task.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK) && !form.getTitle().equals(task.getTitle())){
      Boolean existSubtask = taskRepository.existsBySimulationIdAndNameAndTitle(task.getSimulation().getId(),
          form.getName(), form.getTitle());
      if (existSubtask){
        throw new BadRequestException("Tiêu đề nhiệm vụ phụ đã tồn tại", ErrorCode.TASK_ERROR_EXIST);
      }
    }

    if (StringUtils.isNotBlank(task.getImagePath())
        && StringUtils.isNotBlank(form.getImagePath())
        && !task.getImagePath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)
        && !task.getImagePath().equals(form.getImagePath())){
      userBaseApiService.deleteByFilePath(task.getImagePath());
    }

    if (StringUtils.isNotBlank(task.getFilePath())
        && StringUtils.isNotBlank(form.getFilePath())
        && !task.getFilePath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)
        && !task.getFilePath().equals(form.getFilePath())){
      userBaseApiService.deleteByFilePath(task.getFilePath());
    }

    if (StringUtils.isNotBlank(task.getVideoPath())
        && StringUtils.isNotBlank(form.getVideoPath())
        && !task.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)
        && !task.getVideoPath().equals(form.getVideoPath())){
      userBaseApiService.deleteByFilePath(task.getVideoPath());
      task.setVideoState(ITDreamConstant.STATE_TASK_PROCESSING);
    }

    taskMapper.fromUpdateTaskFormToEntity(form, task);
    if (StringUtils.isNotBlank(form.getVideoPath())
        && form.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      task.setVideoState(ITDreamConstant.STATE_TASK_DONE);
    }
    taskRepository.save(task);

    if (StringUtils.isNotBlank(task.getVideoPath())
        && !task.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(task.getId());
      data.setKind(ITDreamConstant.KIND_TASK);
      data.setUrl(form.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }

    Simulation simulation = task.getSimulation();
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Cập nhật nhiệm vụ thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    taskService.deleteFileInTask(task);
    taskService.deleteAllTask(task);
    Simulation simulation = task.getSimulation();
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Xóa nhiệm vụ thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update-order", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_UO')")
  public ApiMessageDto<String> updateOrder(@Valid @RequestBody UpdateTaskPositionForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy nhiệm vụ", ErrorCode.TASK_ERROR_NOT_FOUND));
    if (task.getKind().equals(ITDreamConstant.TASK_KIND_TASK)){
      taskService.updateTaskPosition(task, form);
    } else {
      taskService.updateSubtaskPosition(task, form);
    }
    apiMessageDto.setMessage("Cập nhật thứ tự thành công");
    return apiMessageDto;
  }
}