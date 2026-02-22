package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.feedback.FeedbackClientDto;
import com.base.auth.dto.feedback.FeedbackDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.feedback.CreateFeedbackForm;
import com.base.auth.form.feedback.UpdateFeedbackForm;
import com.base.auth.mapper.FeedbackMapper;
import com.base.auth.model.Course;
import com.base.auth.model.CourseEnrollment;
import com.base.auth.model.Feedback;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.FeedbackCriteria;
import com.base.auth.repository.CourseEnrollmentRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.FeedbackRepository;
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
@RequestMapping("/v1/feedback")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class FeedbackController extends ABasicController{
  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  CourseEnrollmentRepository courseEnrollmentRepository;

  @Autowired
  FeedbackMapper feedbackMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FB_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateFeedbackForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Student not found", ErrorCode.USER_ERROR_NOT_FOUND));
    Course course = courseRepository.findById(form.getCourseId())
        .orElseThrow(() -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    Boolean existFeedback = feedbackRepository.existsByStudentIdAndCourseId(student.getId(), course.getId());
    if (existFeedback){
      throw new BadRequestException("Feedback already exist", ErrorCode.FEEDBACK_ERROR_EXIST);
    }
    CourseEnrollment courseEnrollment = courseEnrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
        .orElseThrow(() -> new NotFoundException("Course enrollment not found", ErrorCode.COURSE_ENROLLMENT_ERROR_NOT_FOUND));
    if (!ITDreamConstant.COURSE_ENROLLMENT_COMPLETED.equals(courseEnrollment.getStatus())){
      throw new BadRequestException("Student has not completed this course", ErrorCode.COURSE_ENROLLMENT_ERROR_NOT_CREATE);
    }
    Feedback feedback = feedbackMapper.fromCreateFeedbackFormToEntity(form);
    feedback.setStudent(student);
    feedback.setCourse(course);
    feedbackRepository.save(feedback);
    apiMessageDto.setMessage("Create feedback success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FB_L')")
  public ApiMessageDto<ResponseListDto<List<FeedbackDto>>> list(FeedbackCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<FeedbackDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<FeedbackDto>> responseListDto = new ResponseListDto<>();
    Page<Feedback> feedbacks = feedbackRepository.findAll(criteria.getSpecification(), pageable);
    List<FeedbackDto> feedbackDtos = feedbackMapper.fromEntityToFeedbackDtoList(feedbacks.getContent());
    responseListDto.setContent(feedbackDtos);
    responseListDto.setTotalElements(feedbacks.getTotalElements());
    responseListDto.setTotalPages(feedbacks.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<FeedbackClientDto>>> listByClient(FeedbackCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<FeedbackClientDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<FeedbackClientDto>> responseListDto = new ResponseListDto<>();
    Page<Feedback> feedbacks = feedbackRepository.findAll(criteria.getSpecification(), pageable);
    List<FeedbackClientDto> feedbackDtos = feedbackMapper.fromEntityToFeedbackClientDtoList(feedbacks.getContent());
    responseListDto.setContent(feedbackDtos);
    responseListDto.setTotalElements(feedbacks.getTotalElements());
    responseListDto.setTotalPages(feedbacks.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FB_ST_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateFeedbackForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Feedback feedback = feedbackRepository.findById(form.getId())
            .orElseThrow(() -> new NotFoundException("Feedback not found", ErrorCode.FEEDBACK_ERROR_NOT_FOUND));
    if (!feedback.getStudent().getId().equals(getCurrentUser())){
      throw new UnauthorizationException("Feedback was not created by this student");
    }
    feedbackMapper.fromUpdateFeedbackFormToEntity(form, feedback);
    feedbackRepository.save(feedback);
    apiMessageDto.setMessage("Update feedback success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FB_ST_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Feedback feedback = feedbackRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Feedback not found", ErrorCode.FEEDBACK_ERROR_NOT_FOUND));
    if (!feedback.getStudent().getId().equals(getCurrentUser())){
      throw new UnauthorizationException("Feedback was not created by this student");
    }
    feedbackRepository.delete(feedback);
    apiMessageDto.setMessage("Delete feedback success");
    return apiMessageDto;
  }
}
