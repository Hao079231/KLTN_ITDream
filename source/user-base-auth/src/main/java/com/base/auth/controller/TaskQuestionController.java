package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.taskQuestion.TaskQuestionDto;
import com.base.auth.dto.taskQuestion.TaskQuestionEducatorDto;
import com.base.auth.dto.taskQuestion.TaskQuestionStudentDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.taskQuestion.CreateTaskQuestionForm;
import com.base.auth.form.taskQuestion.UpdateTaskQuestionForm;
import com.base.auth.mapper.TaskQuestionMapper;
import com.base.auth.model.Simulation;
import com.base.auth.model.Task;
import com.base.auth.model.TaskQuestion;
import com.base.auth.model.criteria.TaskQuestionCriteria;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.TaskRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import java.util.List;
import java.util.Objects;
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
@RequestMapping("/v1/task_question")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class TaskQuestionController extends ABasicController{
  @Autowired
  TaskQuestionRepository taskQuestionRepository;

  @Autowired
  TaskRepository taskRepository;

  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

  @Autowired
  TaskQuestionMapper taskQuestionMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TQ_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateTaskQuestionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(form.getTaskId()).orElseThrow(()
    -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    Boolean existQuestion = taskQuestionRepository.existsByQuestionAndOptionsAndTaskId(form.getQuestion(),form.getOptions(), form.getTaskId());
    if (existQuestion){
      throw new BadRequestException("Question already exist", ErrorCode.TASK_QUESTION_ERROR_EXIST);
    }

    TaskQuestion taskQuestion = taskQuestionMapper.fromCreateTaskQuestionFormToEntity(form);
    taskQuestion.setTask(task);
    taskQuestionRepository.save(taskQuestion);
    if (task.getTotalQuestion() == null || task.getTotalQuestion() == 0){
      task.setTotalQuestion(1);
      task.setTotalError(1);
    } else {
      task.setTotalQuestion(task.getTotalQuestion() + 1);
      task.setTotalError((int) Math.ceil((double) (task.getTotalQuestion() + 1) / 2));
    }
    taskRepository.save(task);
    Simulation simulation = task.getSimulation();
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Create task question success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TQ_L')")
  public ApiMessageDto<ResponseListDto<List<TaskQuestionDto>>> list(TaskQuestionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskQuestionDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskQuestionDto>> responseListDto = new ResponseListDto<>();
    Page<TaskQuestion> lessonQuestions = taskQuestionRepository.findAll(criteria.getSpecification(), pageable);
    List<TaskQuestionDto> taskQuestionDtos = taskQuestionMapper.fromEntityToTaskQuestionDtoList(lessonQuestions.getContent());
    responseListDto.setContent(taskQuestionDtos);
    responseListDto.setTotalElements(lessonQuestions.getTotalElements());
    responseListDto.setTotalPages(lessonQuestions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson question success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TQ_ED_L')")
  public ApiMessageDto<ResponseListDto<List<TaskQuestionEducatorDto>>> listByEducator(TaskQuestionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskQuestionEducatorDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskQuestionEducatorDto>> responseListDto = new ResponseListDto<>();
    Page<TaskQuestion> lessonQuestions = taskQuestionRepository.findAll(criteria.getSpecification(), pageable);
    List<TaskQuestionEducatorDto> lessonQuestionDtos = taskQuestionMapper.fromEntityToTaskQuestionEducatorDtoList(lessonQuestions.getContent());
    responseListDto.setContent(lessonQuestionDtos);
    responseListDto.setTotalElements(lessonQuestions.getTotalElements());
    responseListDto.setTotalPages(lessonQuestions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson question success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TQ_ST_L')")
  public ApiMessageDto<ResponseListDto<List<TaskQuestionStudentDto>>> listByStudent(
      TaskQuestionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<TaskQuestionStudentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<TaskQuestionStudentDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.SIMULATION_STATUS_ACTIVE);
    Page<TaskQuestion> lessonQuestions = taskQuestionRepository.findAll(criteria.getSpecification(), pageable);
    List<TaskQuestionStudentDto> lessonQuestionDtos = taskQuestionMapper.fromEntityToTaskQuestionStudentDtoList(lessonQuestions.getContent());
    responseListDto.setContent(lessonQuestionDtos);
    responseListDto.setTotalElements(lessonQuestions.getTotalElements());
    responseListDto.setTotalPages(lessonQuestions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson question success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TQ_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateTaskQuestionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    TaskQuestion taskQuestion = taskQuestionRepository.findById(form.getId()).orElseThrow(()
    -> new NotFoundException("Task question not found", ErrorCode.TASK_QUESTION_ERROR_NOT_FOUND));
    if (!Objects.equals(taskQuestion.getQuestion(), form.getQuestion()) || !Objects.equals(taskQuestion.getOptions(), form.getOptions())){
      Boolean existQuestion = taskQuestionRepository.existsByQuestionAndOptionsAndTaskId(form.getQuestion(),
          form.getOptions(), taskQuestion.getTask().getId());
      if (existQuestion) {
        throw new BadRequestException("Task question already exist", ErrorCode.TASK_QUESTION_ERROR_EXIST);
      }
    }

    taskQuestionMapper.fromUpdateTaskQuestionFormToEntity(form, taskQuestion);
    taskQuestionRepository.save(taskQuestion);
    Simulation simulation = taskQuestion.getTask().getSimulation();
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Update lesson question success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('TQ_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    TaskQuestion taskQuestion = taskQuestionRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Task question not found", ErrorCode.TASK_QUESTION_ERROR_NOT_FOUND));

    reviewSubmissionRepository.deleteAllByTaskQuestionId(id);
    questionQuizHistoryRepository.deleteAllByTaskQuestionId(id);
    studentSubmissionRepository.deleteAllByTaskQuestionId(id);
    Task task = taskQuestion.getTask();
    task.setTotalError((int) ((double)(task.getTotalError() - 1) / 2));
    task.setTotalQuestion(task.getTotalQuestion() - 1);
    taskRepository.save(task);

    Simulation simulation = task.getSimulation();
    simulation.setStatus(ITDreamConstant.SIMULATION_STATUS_WAITING_APPROVE);
    simulationRepository.save(simulation);
    taskQuestionRepository.delete(taskQuestion);
    apiMessageDto.setMessage("Delete task question success");
    return apiMessageDto;
  }
}
