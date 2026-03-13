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
import com.base.auth.mapper.TaskMapper;
import com.base.auth.model.Simulation;
import com.base.auth.model.Task;
import com.base.auth.model.criteria.TaskCriteria;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.TaskRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.service.TaskService;
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
@RequestMapping("/v1/task")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class TaskController extends ABasicController{
  @Autowired
  TaskRepository taskRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  TaskQuestionRepository taskQuestionRepository;

  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

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
  public ApiMessageDto<String> create(@Valid @RequestBody CreateTaskForm form, BindingResult bindingResult){
    if(!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(form.getSimulationId())
        .orElseThrow(() -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (form.getKind().equals(ITDreamConstant.TASK_KIND_TASK)){
      Boolean existTask = taskRepository.existsBySimulationIdAndName(form.getSimulationId(), form.getName());
      if (existTask){
        throw new BadRequestException("Task name already exist", ErrorCode.TASK_ERROR_EXIST);
      }
    } else if (form.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      Boolean existSubtask = taskRepository.existsBySimulationIdAndNameAndTitle(form.getSimulationId(), form.getName(), form.getTitle());
      if (existSubtask){
        throw new BadRequestException("Subtask title already exist", ErrorCode.TASK_ERROR_EXIST);
      }
    }

    Task task = taskMapper.fromCreateTaskFormToEntity(form);
    task.setSimulation(simulation);
    if (form.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      Task parent = taskRepository.findById(form.getParentId())
          .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.TASK_ERROR_NOT_FOUND));
      task.setParent(parent);
    }
    if (StringUtils.isNotBlank(form.getVideoPath()) && form.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      task.setVideoState(ITDreamConstant.STATE_TASK_PROCESSING);
    } else {
      task.setVideoState(ITDreamConstant.STATE_TASK_DONE);
    }
    taskRepository.save(task);

    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);

    if (StringUtils.isNotBlank(task.getVideoPath()) && task.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(task.getId());
      data.setUrl(task.getVideoPath());
      data.setKind(ITDreamConstant.KIND_TASK);
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setMessage("Create task success");
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
    apiMessageDto.setMessage("Get list task success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_V')")
  public ApiMessageDto<TaskDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<TaskDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    apiMessageDto.setData(taskMapper.fromEntityToTaskDto(task));
    apiMessageDto.setMessage("Get task success");
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
    apiMessageDto.setMessage("Get list task success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_V')")
  public ApiMessageDto<TaskEducatorDto> getByEducator(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<TaskEducatorDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    apiMessageDto.setData(taskMapper.fromEntityToTaskEducatorDto(task));
    apiMessageDto.setMessage("Get task success");
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
    apiMessageDto.setMessage("Get list task success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ST_V')")
  public ApiMessageDto<TaskStudentDto> getByStudent(@PathVariable("id") Long id){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<TaskStudentDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    apiMessageDto.setData(taskMapper.fromEntityToTaskStudentDto(task));
    apiMessageDto.setMessage("Get task success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateTaskForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(form.getId()).orElseThrow(()
    -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));

    if (task.getKind().equals(ITDreamConstant.TASK_KIND_TASK)){
      Boolean existTask = taskRepository.existsBySimulationIdAndName(task.getSimulation().getId(), form.getName());
      if (existTask){
        throw new BadRequestException("Task name already exist", ErrorCode.TASK_ERROR_EXIST);
      }
    } else if (task.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      Boolean existSubtask = taskRepository.existsBySimulationIdAndNameAndTitle(task.getSimulation().getId(),
          form.getName(), form.getTitle());
      if (existSubtask){
        throw new BadRequestException("Subtask title already exist", ErrorCode.TASK_ERROR_EXIST);
      }
    }

    if (StringUtils.isNotBlank(form.getImagePath()) &&
        task.getImagePath().toLowerCase().startsWith(File.separator + "image") &&
        !Objects.equals(form.getImagePath(), task.getImagePath())){
      userBaseApiService.deleteByFilePath(task.getImagePath());
      task.setImagePath(form.getImagePath());
    }

    if (StringUtils.isNotBlank(form.getFilePath()) &&
        task.getFilePath().toLowerCase().startsWith(File.separator + "document") &&
        !Objects.equals(form.getFilePath(), task.getFilePath())){
      userBaseApiService.deleteByFilePath(task.getFilePath());
      task.setFilePath(form.getFilePath());
    }

    if (StringUtils.isNotBlank(form.getVideoPath()) &&
        task.getVideoPath().toLowerCase().startsWith(File.separator + "video") &&
        !Objects.equals(form.getVideoPath(), task.getVideoPath())){
      userBaseApiService.deleteByFilePath(task.getVideoPath());
      task.setVideoPath(form.getVideoPath());
    }

    taskMapper.fromUpdateTaskFormToEntity(form, task);
    taskRepository.save(task);

    if (StringUtils.isNotBlank(form.getVideoPath()) && task.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
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
    apiMessageDto.setMessage("Update task success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TA_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    if (StringUtils.isNotBlank(task.getImagePath()) &&
        task.getImagePath().toLowerCase().startsWith(File.separator + "image")){
      userBaseApiService.deleteByFilePath(task.getImagePath());
    }

    if (StringUtils.isNotBlank(task.getFilePath()) &&
        task.getFilePath().toLowerCase().startsWith(File.separator + "document")){
      userBaseApiService.deleteByFilePath(task.getFilePath());
    }

    if (StringUtils.isNotBlank(task.getVideoPath()) &&
        task.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      userBaseApiService.deleteByFilePath(task.getVideoPath());
    }

    taskService.deleteAllByTask(task);
    Simulation simulation = task.getSimulation();
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Delete task success");
    return apiMessageDto;
  }
}
