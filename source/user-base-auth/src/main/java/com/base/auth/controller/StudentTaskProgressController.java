package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.achievement.AchievementDisplayDto;
import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDisplayDto;
import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.studentTaskProgress.CreateStudentTaskProgressForm;
import com.base.auth.form.studentTaskProgress.RequestTaskIdForm;
import com.base.auth.mapper.StudentTaskProgressMapper;
import com.base.auth.model.Achievement;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.Task;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.StudentTaskProgressCriteria;
import com.base.auth.repository.AchievementRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.TaskRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/task_progress")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StudentTaskProgressController extends ABasicController{
  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  SimulationEnrollmentRepository simulationEnrollmentRepository;

  @Autowired
  TaskRepository taskRepository;

  @Autowired
  TaskQuestionRepository taskQuestionRepository;

  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  AchievementRepository achievementRepository;

  @Autowired
  StudentTaskProgressMapper studentTaskProgressMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateStudentTaskProgressForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existStudentTaskProgress = studentTaskProgressRepository.existsBySimulationEnrollmentIdAndStatus(form.getSimulationEnrollmentId(), ITDreamConstant.STUDENT_TASK_PROGRESS_IN_PROGRESS);
    if (existStudentTaskProgress){
      throw new BadRequestException("Please complete the previous task", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_CREATE);
    }
    Task task = taskRepository.findById(form.getTaskId()).orElseThrow(()
    -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    SimulationEnrollment simulationEnrollment = simulationEnrollmentRepository.findById(form.getSimulationEnrollmentId()).orElseThrow(()
    -> new NotFoundException("Simulation enrollment not found", ErrorCode.SIMULATION_ENROLLMENT_ERROR_NOT_FOUND));
    if (!Objects.equals(task.getSimulation().getId(), simulationEnrollment.getSimulation().getId())){
      throw new BadRequestException("Cannot create task progress", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_CREATE);
    }

    StudentTaskProgress studentTaskProgress = new StudentTaskProgress();
    studentTaskProgress.setStatus(ITDreamConstant.STUDENT_TASK_PROGRESS_IN_PROGRESS);
    studentTaskProgress.setTask(task);
    studentTaskProgress.setSimulationEnrollment(simulationEnrollment);
    studentTaskProgressRepository.save(studentTaskProgress);
    apiMessageDto.setMessage("Create task progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_L')")
  public ApiMessageDto<ResponseListDto<List<StudentTaskProgressDto>>> list(
      StudentTaskProgressCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentTaskProgressDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentTaskProgressDto>> responseListDto = new ResponseListDto<>();
    Page<StudentTaskProgress> studentTaskProgresses = studentTaskProgressRepository.findAll(criteria.getSpecification(), pageable);
    List<StudentTaskProgressDto> lessonProgressDtos = studentTaskProgressMapper.fromEntityToStudentTaskProgressDtoList(studentTaskProgresses.getContent());
    responseListDto.setContent(lessonProgressDtos);
    responseListDto.setTotalElements(studentTaskProgresses.getTotalElements());
    responseListDto.setTotalPages(studentTaskProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_L')")
  public ApiMessageDto<ResponseListDto<List<StudentTaskProgressDisplayDto>>> listByStudent(
      StudentTaskProgressCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentTaskProgressDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentTaskProgressDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<StudentTaskProgress> studentTaskProgresses = studentTaskProgressRepository.findAll(criteria.getSpecification(), pageable);
    List<StudentTaskProgressDisplayDto> lessonProgressDtos = studentTaskProgressMapper.fromEntityToStudentTaskProgressDisplayDtoList(studentTaskProgresses.getContent());
    responseListDto.setContent(lessonProgressDtos);
    responseListDto.setTotalElements(studentTaskProgresses.getTotalElements());
    responseListDto.setTotalPages(studentTaskProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson progress success");
    return apiMessageDto;
  }

  @PutMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_CPL')")
  public ApiMessageDto<AchievementDisplayDto> complete(@Valid @RequestBody RequestTaskIdForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<AchievementDisplayDto> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(form.getTaskId())
        .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findByTaskIdAndSimulationEnrollmentStudentId(form.getTaskId(), getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    Integer totalQuestion = taskQuestionRepository.countByTaskId(task.getId());
    Integer totalSubmission = studentSubmissionRepository.countByStudentTaskProgressId(studentTaskProgress.getId());
    Integer distinctAnswered = studentSubmissionRepository.countDistinctQuestionByStudentTaskProgress(studentTaskProgress.getId());
    if (!Objects.equals(totalQuestion, totalSubmission) || !Objects.equals(totalQuestion, distinctAnswered)){
      throw new BadRequestException("Not all questions are answered correctly", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_COMPLETED);
    }

    Student student = studentTaskProgress.getSimulationEnrollment().getStudent();
    studentRepository.save(student);

    SimulationEnrollment simulationEnrollment = studentTaskProgress.getSimulationEnrollment();
    studentTaskProgress.setErrorCount(ITDreamConstant.RESTART_ERROR_COUNT);
    studentTaskProgress.setStatus(ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED);
    studentTaskProgressRepository.saveAndFlush(studentTaskProgress);

    Integer totalLesson = taskRepository.countTaskBySimulationId(task.getSimulation().getId());
    Integer completedLesson = studentTaskProgressRepository.countCompletedTaskInSimulation(
        simulationEnrollment.getId(), task.getSimulation().getId(), ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED);
    Float progress =  ((float) completedLesson / totalLesson) * 100;
    simulationEnrollment.setProgress(progress);
    if (completedLesson.equals(totalLesson)){
      simulationEnrollment.setStatus(ITDreamConstant.SIMULATION_ENROLLMENT_COMPLETED);
      simulationEnrollmentRepository.save(simulationEnrollment);

      Achievement achievement = new Achievement();
      achievement.setSimulation(simulationEnrollment.getSimulation());
      achievement.setStudent(simulationEnrollment.getStudent());
      achievementRepository.save(achievement);

      AchievementDisplayDto achievementDisplayDto = new AchievementDisplayDto();
      achievementDisplayDto.setId(achievement.getId());
      achievementDisplayDto.setUsername(simulationEnrollment.getStudent().getAccount().getUsername());
      achievementDisplayDto.setSimulationTitle(simulationEnrollment.getSimulation().getTitle());
      apiMessageDto.setData(achievementDisplayDto);
      apiMessageDto.setMessage("Complete simulation");
      return apiMessageDto;
    }
    simulationEnrollmentRepository.save(simulationEnrollment);
    apiMessageDto.setMessage("Complete task");
    return apiMessageDto;
  }

  @PutMapping(value = "/reset", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSP_ST_RS')")
  public ApiMessageDto<String> reset(@Valid @RequestBody RequestTaskIdForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Task task = taskRepository.findById(form.getTaskId())
        .orElseThrow(() -> new NotFoundException("Task not found", ErrorCode.TASK_ERROR_NOT_FOUND));
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findByTaskIdAndSimulationEnrollmentStudentId(form.getTaskId(), getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    Integer submission = studentSubmissionRepository.countByStudentTaskProgressId(studentTaskProgress.getId());
    if (submission > 0){
      studentSubmissionRepository.deleteAllByStudentTaskProgressId(studentTaskProgress.getId());
    }
    questionQuizHistoryRepository.deleteAllByStudentTaskProgressTaskId(studentTaskProgress.getId());
    studentTaskProgress.setErrorCount(ITDreamConstant.RESTART_ERROR_COUNT);
    studentTaskProgressRepository.save(studentTaskProgress);
    apiMessageDto.setMessage("Reset task progress success");
    return apiMessageDto;
  }
}
