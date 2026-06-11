package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.achievement.AchievementDisplayDto;
import com.base.auth.dto.studentSubmission.StudentSubmissionDisplayDto;
import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDisplayDto;
import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDto;
import com.base.auth.dto.studentTaskProgress.StudentTaskProgressDetailDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.studentTaskProgress.CreateStudentTaskProgressForm;
import com.base.auth.form.studentTaskProgress.RequestTaskIdForm;
import com.base.auth.mapper.StudentSubmissionMapper;
import com.base.auth.mapper.StudentTaskProgressMapper;
import com.base.auth.model.Achievement;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.StudentSubmission;
import com.base.auth.model.Task;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.StudentSubmissionCriteria;
import com.base.auth.model.criteria.StudentTaskProgressCriteria;
import com.base.auth.repository.AchievementRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.TaskRepository;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  AchievementRepository achievementRepository;

  @Autowired
  StudentTaskProgressMapper studentTaskProgressMapper;

  @Autowired
  StudentSubmissionMapper studentSubmissionMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateStudentTaskProgressForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existStudentTaskProgress = studentTaskProgressRepository.existsBySimulationEnrollmentIdAndTask_KindAndStatus(form.getSimulationEnrollmentId(), ITDreamConstant.TASK_KIND_SUBTASK, ITDreamConstant.STUDENT_TASK_PROGRESS_IN_PROGRESS);
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
    List<StudentTaskProgressDto> taskProgressDtos = studentTaskProgressMapper.fromEntityToStudentTaskProgressDtoList(studentTaskProgresses.getContent());
    responseListDto.setContent(taskProgressDtos);
    responseListDto.setTotalElements(studentTaskProgresses.getTotalElements());
    responseListDto.setTotalPages(studentTaskProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list task progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_L')")
  public ApiMessageDto<ResponseListDto<List<StudentTaskProgressDisplayDto>>> listByStudent(
      StudentTaskProgressCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentTaskProgressDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentTaskProgressDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<StudentTaskProgress> studentTaskProgresses = studentTaskProgressRepository.findAll(criteria.getSpecification(), pageable);
    List<StudentTaskProgressDisplayDto> taskProgressDtos = studentTaskProgressMapper.fromEntityToStudentTaskProgressDisplayDtoList(studentTaskProgresses.getContent());
    responseListDto.setContent(taskProgressDtos);
    responseListDto.setTotalElements(studentTaskProgresses.getTotalElements());
    responseListDto.setTotalPages(studentTaskProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list task progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_L')")
  public ApiMessageDto<ResponseListDto<List<StudentTaskProgressDisplayDto>>> listByEducator(
      StudentTaskProgressCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentTaskProgressDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentTaskProgressDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED);
    Page<StudentTaskProgress> studentTaskProgresses = studentTaskProgressRepository.findAll(criteria.getSpecification(), pageable);
    List<StudentTaskProgressDisplayDto> taskProgressDtos = studentTaskProgressMapper.fromEntityToStudentTaskProgressDisplayDtoList(studentTaskProgresses.getContent());
    responseListDto.setContent(taskProgressDtos);
    responseListDto.setTotalElements(studentTaskProgresses.getTotalElements());
    responseListDto.setTotalPages(studentTaskProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list task progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_V')")
  public ApiMessageDto<StudentTaskProgressDetailDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<StudentTaskProgressDetailDto> apiMessageDto = new ApiMessageDto<>();
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Student task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    StudentTaskProgressDetailDto studentTaskProgressDetailDto = studentTaskProgressMapper.fromEntityToStudentTaskProgressStudentDto(studentTaskProgress);

    ResponseListDto<List<StudentSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    StudentSubmissionCriteria studentSubmissionCriteria = new StudentSubmissionCriteria();
    studentSubmissionCriteria.setStudentTaskProgressId(studentTaskProgress.getId());
    Pageable pageable = PageRequest.of(0, 100);
    Page<StudentSubmission> studentSubmissions = studentSubmissionRepository.findAll(studentSubmissionCriteria.getSpecification(), pageable);
    List<StudentSubmissionDisplayDto> studentSubmissionDisplayDtos = studentSubmissionMapper.fromEntityToStudentSubmissionDisplayDtoList(studentSubmissions.getContent());
    responseListDto.setContent(studentSubmissionDisplayDtos);
    responseListDto.setTotalElements(studentSubmissions.getTotalElements());
    responseListDto.setTotalPages(studentSubmissions.getTotalPages());
    studentTaskProgressDetailDto.setStudentSubmission(responseListDto);
    apiMessageDto.setData(studentTaskProgressDetailDto);
    apiMessageDto.setMessage("Get student task progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ST_V')")
  public ApiMessageDto<StudentTaskProgressDetailDto> getByStudent(@PathVariable("id") Long id){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<StudentTaskProgressDetailDto> apiMessageDto = new ApiMessageDto<>();
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Student task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    StudentTaskProgressDetailDto studentTaskProgressDetailDto = studentTaskProgressMapper.fromEntityToStudentTaskProgressStudentDto(studentTaskProgress);

    ResponseListDto<List<StudentSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    StudentSubmissionCriteria studentSubmissionCriteria = new StudentSubmissionCriteria();
    studentSubmissionCriteria.setStudentTaskProgressId(studentTaskProgress.getId());
    Pageable pageable = PageRequest.of(0, 100);
    Page<StudentSubmission> studentSubmissions = studentSubmissionRepository.findAll(studentSubmissionCriteria.getSpecification(), pageable);
    List<StudentSubmissionDisplayDto> studentSubmissionDisplayDtos = studentSubmissionMapper.fromEntityToStudentSubmissionDisplayDtoList(studentSubmissions.getContent());
    responseListDto.setContent(studentSubmissionDisplayDtos);
    responseListDto.setTotalElements(studentSubmissions.getTotalElements());
    responseListDto.setTotalPages(studentSubmissions.getTotalPages());
    studentTaskProgressDetailDto.setStudentSubmission(responseListDto);
    apiMessageDto.setData(studentTaskProgressDetailDto);
    apiMessageDto.setMessage("Get student task progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('STP_ED_V')")
  public ApiMessageDto<StudentTaskProgressDetailDto> getByEducator(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<StudentTaskProgressDetailDto> apiMessageDto = new ApiMessageDto<>();
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Student task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    StudentTaskProgressDetailDto studentTaskProgressDetailDto = studentTaskProgressMapper.fromEntityToStudentTaskProgressStudentDto(studentTaskProgress);

    ResponseListDto<List<StudentSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    StudentSubmissionCriteria studentSubmissionCriteria = new StudentSubmissionCriteria();
    studentSubmissionCriteria.setStudentId(studentTaskProgress.getId());
    Pageable pageable = PageRequest.of(0, 100);
    Page<StudentSubmission> studentSubmissions = studentSubmissionRepository.findAll(studentSubmissionCriteria.getSpecification(), pageable);
    List<StudentSubmissionDisplayDto> studentSubmissionDisplayDtos = studentSubmissionMapper.fromEntityToStudentSubmissionDisplayDtoList(studentSubmissions.getContent());
    responseListDto.setContent(studentSubmissionDisplayDtos);
    responseListDto.setTotalElements(studentSubmissions.getTotalElements());
    responseListDto.setTotalPages(studentSubmissions.getTotalPages());
    studentTaskProgressDetailDto.setStudentSubmission(responseListDto);
    apiMessageDto.setData(studentTaskProgressDetailDto);
    apiMessageDto.setMessage("Get student task progress success");
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
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findByTaskIdAndSimulationEnrollmentStudentId(task.getId(), getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));

    // Kiểm tra xem một subtask loại task truyền vào có kind là gì để kiểm tra khi kind là task hoặc subtask
    if (Objects.equals(task.getKind(), ITDreamConstant.TASK_KIND_SUBTASK)){
      Boolean existTaskQuestion = taskQuestionRepository.existsByTaskId(task.getId());
      if (existTaskQuestion){
        Integer totalQuestion = taskQuestionRepository.countByTaskId(task.getId());
        Integer totalSubmission = studentSubmissionRepository.countByStudentTaskProgressId(studentTaskProgress.getId());
        if (!Objects.equals(totalQuestion, totalSubmission)){
          throw new BadRequestException("Not all questions are answered correctly", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_COMPLETED);
        }
      }
      studentTaskProgress.setStatus(ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED);
      studentTaskProgress.setErrorCount(ITDreamConstant.RESTART_ERROR_COUNT);
      studentTaskProgressRepository.save(studentTaskProgress);
    } else {
      Integer totalSubtask = taskRepository.countByParentId(task.getId());
      Integer totalCompletedSubtask = studentTaskProgressRepository.countCompletedSubtaskInTask(task.getId(), ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED, studentTaskProgress.getSimulationEnrollment().getId());
      if (!Objects.equals(totalCompletedSubtask, totalSubtask)){
        throw new BadRequestException("Not all subtasks are completed", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_COMPLETED);
      }
      studentTaskProgress.setStatus(ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED);
      studentTaskProgress.setErrorCount(ITDreamConstant.RESTART_ERROR_COUNT);
      studentTaskProgressRepository.save(studentTaskProgress);
    }

    // Sau khi cập nhật tiến trình của task thì sang cập nhật tiến trình của simulation
    SimulationEnrollment simulationEnrollment = studentTaskProgress.getSimulationEnrollment();
    Integer totalTask = taskRepository.countTaskBySimulationId(task.getSimulation().getId());
    Integer completedTask = studentTaskProgressRepository.countCompletedTaskInSimulation(
        simulationEnrollment.getId(), task.getSimulation().getId(), ITDreamConstant.STUDENT_TASK_PROGRESS_COMPLETED);
    Float progress =  ((float) completedTask / totalTask) * 100;
    simulationEnrollment.setProgress(progress);
    if (completedTask.equals(totalTask)){
      simulationEnrollment.setStatus(ITDreamConstant.SIMULATION_ENROLLMENT_COMPLETED);
      simulationEnrollmentRepository.save(simulationEnrollment);

      // Cần kiểm tra xem student đã có thành tựu trong simulation này chưa, nếu có rồi thì báo thành công thôi, không có thì tạo mới
      Boolean existAchievement = achievementRepository.existsBySimulationIdAndStudentId(simulationEnrollment.getSimulation().getId(), simulationEnrollment.getStudent().getId());
      if (existAchievement){
        apiMessageDto.setMessage("Complete simulation");
        return apiMessageDto;
      }

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
    if (Objects.equals(task.getKind(), ITDreamConstant.TASK_KIND_TASK)){
      throw new BadRequestException("Cannot reset task", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_RESET);
    }
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findByTaskIdAndSimulationEnrollmentStudentId(form.getTaskId(), getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    studentSubmissionRepository.deleteQuestionSubmissionsByProgressId(studentTaskProgress.getId());
    studentTaskProgress.setStatus(ITDreamConstant.STUDENT_TASK_PROGRESS_IN_PROGRESS);
    studentTaskProgress.setErrorCount(ITDreamConstant.RESTART_ERROR_COUNT);
    studentTaskProgressRepository.save(studentTaskProgress);
    apiMessageDto.setMessage("Reset task progress success");
    return apiMessageDto;
  }
}
