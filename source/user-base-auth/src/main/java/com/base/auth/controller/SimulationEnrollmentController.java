package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.simulationEnrollment.SimulationEnrollmentDisplayDto;
import com.base.auth.dto.simulationEnrollment.SimulationEnrollmentDto;
import com.base.auth.dto.simulationEnrollment.StudentLessonViewsDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.simulationEnrollment.CreateSimulationEnrollmentForm;
import com.base.auth.mapper.SimulationEnrollmentMapper;
import com.base.auth.model.Simulation;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.SimulationEnrollmentCriteria;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/simulation_enrollment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SimulationEnrollmentController extends ABasicController{
  @Autowired
  SimulationEnrollmentRepository simulationEnrollmentRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  SimulationEnrollmentMapper simulationEnrollmentMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SE_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateSimulationEnrollmentForm form,  BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existSimulationEnrollment = simulationEnrollmentRepository.existsBySimulationIdAndStudentId(form.getSimulationId(), getCurrentUser());
    if (existSimulationEnrollment){
      throw new BadRequestException("Simulation enrollment already exist", ErrorCode.SIMULATION_ENROLLMENT_ERROR_EXIST);
    }
    Student student = studentRepository.findById(getCurrentUser()).orElseThrow(()
    -> new NotFoundException("Student not found", ErrorCode.USER_ERROR_NOT_FOUND));
    Simulation simulation = simulationRepository.findById(form.getSimulationId()).orElseThrow(()
    -> new NotFoundException("Simulation not found", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    if (!Objects.equals(simulation.getStatus(), ITDreamConstant.SIMULATION_STATUS_ACTIVE)){
      throw new BadRequestException("Cannot create simulation enrollment", ErrorCode.SIMULATION_ENROLLMENT_ERROR_NOT_CREATE);
    }
    SimulationEnrollment simulationEnrollment = new SimulationEnrollment();
    simulationEnrollment.setStatus(ITDreamConstant.SIMULATION_ENROLLMENT_IN_PROGRESS);
    simulationEnrollment.setStudent(student);
    simulationEnrollment.setSimulation(simulation);
    simulationEnrollmentRepository.save(simulationEnrollment);

    if (simulation.getTotalParticipant() == null){
      simulation.setTotalParticipant(1L);
    } else {
      simulation.setTotalParticipant(simulation.getTotalParticipant() + 1);
    }
    simulationRepository.save(simulation);
    apiMessageDto.setMessage("Create simulation enrollment success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SE_L')")
  public ApiMessageDto<ResponseListDto<List<SimulationEnrollmentDto>>> list(
      SimulationEnrollmentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<SimulationEnrollmentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<SimulationEnrollmentDto>> responseListDto = new ResponseListDto<>();
    Page<SimulationEnrollment> simulationEnrollments = simulationEnrollmentRepository.findAll(criteria.getSpecification(), pageable);
    List<SimulationEnrollmentDto> simulationEnrollmentDtos = simulationEnrollmentMapper.fromEntityToSimulationEnrollmentDtoList(simulationEnrollments.getContent());
    responseListDto.setContent(simulationEnrollmentDtos);
    responseListDto.setTotalElements(simulationEnrollments.getTotalElements());
    responseListDto.setTotalPages(simulationEnrollments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list simulation enrollment success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SE_ST_L')")
  public ApiMessageDto<ResponseListDto<List<SimulationEnrollmentDisplayDto>>> listByStudent(
      SimulationEnrollmentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<SimulationEnrollmentDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<SimulationEnrollmentDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStudentId(getCurrentUser());
    Page<SimulationEnrollment> simulationEnrollments = simulationEnrollmentRepository.findAll(criteria.getSpecification(), pageable);
    List<SimulationEnrollmentDisplayDto> courseEnrollmentDtos = simulationEnrollmentMapper.fromEntityToSimulationEnrollmentDisplayDtoList(simulationEnrollments.getContent());
    responseListDto.setContent(courseEnrollmentDtos);
    responseListDto.setTotalElements(simulationEnrollments.getTotalElements());
    responseListDto.setTotalPages(simulationEnrollments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list simulation enrollment success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_complete_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SE_ED_STCL')")
  public ApiMessageDto<ResponseListDto<List<StudentLessonViewsDto>>> listStudentCompleteCourse(
      SimulationEnrollmentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentLessonViewsDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentLessonViewsDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.SIMULATION_ENROLLMENT_COMPLETED);
    Page<SimulationEnrollment> simulationEnrollments = simulationEnrollmentRepository.findAll(criteria.getSpecification(), pageable);
    List<SimulationEnrollment> enrollmentList = simulationEnrollments.getContent();
    List<StudentLessonViewsDto> courseEnrollmentDtos = simulationEnrollmentMapper.fromEntityToStudentLessonViewsDtoList(enrollmentList);
    for (int i = 0; i < enrollmentList.size(); i++) {
      SimulationEnrollment enrollment = enrollmentList.get(i);
      boolean hasUnreviewedTask = studentTaskProgressRepository.existsUnreviewedTask(enrollment.getId(), ITDreamConstant.TASK_KIND_SUBTASK);
      courseEnrollmentDtos.get(i).setIsReviewed(!hasUnreviewedTask);
    }
    responseListDto.setContent(courseEnrollmentDtos);
    responseListDto.setTotalElements(simulationEnrollments.getTotalElements());
    responseListDto.setTotalPages(simulationEnrollments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list student complete simulation success");
    return apiMessageDto;
  }
}
