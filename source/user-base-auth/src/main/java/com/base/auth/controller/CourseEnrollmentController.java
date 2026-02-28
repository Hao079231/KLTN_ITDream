package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.courseEnrollment.CourseEnrollmentDisplayDto;
import com.base.auth.dto.courseEnrollment.CourseEnrollmentDto;
import com.base.auth.dto.courseEnrollment.StudentLessonViewsDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.courseEnrollment.CreateCourseEnrollmentForm;
import com.base.auth.mapper.CourseEnrollmentMapper;
import com.base.auth.model.Course;
import com.base.auth.model.CourseEnrollment;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.CourseEnrollmentCriteria;
import com.base.auth.repository.CorrectAnswerRepository;
import com.base.auth.repository.CourseEnrollmentRepository;
import com.base.auth.repository.CourseRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/course_enrollment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CourseEnrollmentController extends ABasicController{
  @Autowired
  CourseEnrollmentRepository courseEnrollmentRepository;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

  @Autowired
  CourseEnrollmentMapper courseEnrollmentMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CEM_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateCourseEnrollmentForm form,  BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(getCurrentUser()).orElseThrow(()
    -> new NotFoundException("Student not found", ErrorCode.USER_ERROR_NOT_FOUND));
    Course course = courseRepository.findById(form.getCourseId()).orElseThrow(()
    -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(course.getStatus(), ITDreamConstant.COURSE_STATUS_ACTIVE)){
      throw new BadRequestException("Cannot create course enrollment", ErrorCode.COURSE_ENROLLMENT_ERROR_NOT_CREATE);
    }
    CourseEnrollment courseEnrollment = new CourseEnrollment();
    courseEnrollment.setStatus(ITDreamConstant.COURSE_ENROLLMENT_IN_PROGRESS);
    courseEnrollment.setStudent(student);
    courseEnrollment.setCourse(course);
    courseEnrollmentRepository.save(courseEnrollment);

    course.setTotalParticipant(course.getTotalParticipant() + 1);
    courseRepository.save(course);
    apiMessageDto.setMessage("Create course enrollment success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CEM_L')")
  public ApiMessageDto<ResponseListDto<List<CourseEnrollmentDto>>> list(CourseEnrollmentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CourseEnrollmentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CourseEnrollmentDto>> responseListDto = new ResponseListDto<>();
    Page<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.findAll(criteria.getSpecification(), pageable);
    List<CourseEnrollmentDto> courseEnrollmentDtos = courseEnrollmentMapper.fromEntityToCourseEnrollmentDtoList(courseEnrollments.getContent());
    responseListDto.setContent(courseEnrollmentDtos);
    responseListDto.setTotalElements(courseEnrollments.getTotalElements());
    responseListDto.setTotalPages(courseEnrollments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list course enrollment success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CEM_ST_L')")
  public ApiMessageDto<ResponseListDto<List<CourseEnrollmentDisplayDto>>> listByStudent(CourseEnrollmentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CourseEnrollmentDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CourseEnrollmentDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStudentId(getCurrentUser());
    Page<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.findAll(criteria.getSpecification(), pageable);
    List<CourseEnrollmentDisplayDto> courseEnrollmentDtos = courseEnrollmentMapper.fromEntityToCourseEnrollmentDisplayDtoList(courseEnrollments.getContent());
    responseListDto.setContent(courseEnrollmentDtos);
    responseListDto.setTotalElements(courseEnrollments.getTotalElements());
    responseListDto.setTotalPages(courseEnrollments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list course enrollment success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_complete_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CEM_ED_STCL')")
  public ApiMessageDto<ResponseListDto<List<StudentLessonViewsDto>>> listStudentCompleteCourse(CourseEnrollmentCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentLessonViewsDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentLessonViewsDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.COURSE_ENROLLMENT_COMPLETED);
    Page<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.findAll(criteria.getSpecification(), pageable);
    List<CourseEnrollment> enrollmentList = courseEnrollments.getContent();
    List<StudentLessonViewsDto> courseEnrollmentDtos = courseEnrollmentMapper.fromEntityToStudentLessonViewsDtoList(enrollmentList);
    for (int i = 0; i < enrollmentList.size(); i++) {
      CourseEnrollment enrollment = enrollmentList.get(i);
      long totalCorrect = correctAnswerRepository.countByCourseEnrollmentId(enrollment.getId());
      long totalReviewed = reviewSubmissionRepository.countByCourseEnrollmentId(enrollment.getId());
      boolean isReviewed = false;
      if (totalCorrect > 0 && totalCorrect == totalReviewed) {
        isReviewed = true;
      }
      courseEnrollmentDtos.get(i).setIsReviewed(isReviewed);
    }
    responseListDto.setContent(courseEnrollmentDtos);
    responseListDto.setTotalElements(courseEnrollments.getTotalElements());
    responseListDto.setTotalPages(courseEnrollments.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list student complete course success");
    return apiMessageDto;
  }
}
