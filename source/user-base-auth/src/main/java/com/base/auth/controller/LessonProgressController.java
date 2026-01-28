package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.lessonProgress.LessonProgressDisplayDto;
import com.base.auth.dto.lessonProgress.LessonProgressDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.lessonProgress.CreateLessonProgressForm;
import com.base.auth.mapper.LessonProgressMapper;
import com.base.auth.model.CourseEnrollment;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonProgress;
import com.base.auth.model.criteria.LessonProgressCriteria;
import com.base.auth.repository.CourseEnrollmentRepository;
import com.base.auth.repository.LessonProgressRepository;
import com.base.auth.repository.LessonQuestionRepository;
import com.base.auth.repository.LessonRepository;
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
@RequestMapping("/v1/lesson_progress")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class LessonProgressController extends ABasicController{
  @Autowired
  LessonProgressRepository lessonProgressRepository;

  @Autowired
  CourseEnrollmentRepository courseEnrollmentRepository;

  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  LessonQuestionRepository lessonQuestionRepository;

  @Autowired
  LessonProgressMapper lessonProgressMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSP_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateLessonProgressForm form, BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(form.getLessonId()).orElseThrow(()
    -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    CourseEnrollment courseEnrollment = courseEnrollmentRepository.findById(form.getCourseEnrollmentId()).orElseThrow(()
    -> new NotFoundException("Course enrollment not found", ErrorCode.COURSE_ENROLLMENT_NOT_FOUND));
    if (!Objects.equals(lesson.getChapter().getCourse().getId(), courseEnrollment.getCourse().getId())){
      throw new BadRequestException("Cannot create lesson progress", ErrorCode.LESSON_PROGRESS_ERROR_NOT_CREATE);
    }
    Boolean existQuestion = lessonQuestionRepository.existsByLessonId(lesson.getId());
    LessonProgress lessonProgress = new LessonProgress();
    if (!existQuestion){
      lessonProgress.setStatus(ITDreamConstant.LESSON_PROGRESS_COMPLETED);
    } else {
      lessonProgress.setStatus(ITDreamConstant.LESSON_PROGRESS_IN_PROGRESS);
    }
    lessonProgress.setLesson(lesson);
    lessonProgress.setCourseEnrollment(courseEnrollment);
    lessonProgressRepository.save(lessonProgress);
    apiMessageDto.setMessage("Create lesson progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSP_L')")
  public ApiMessageDto<ResponseListDto<List<LessonProgressDto>>> list(LessonProgressCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonProgressDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonProgressDto>> responseListDto = new ResponseListDto<>();
    Page<LessonProgress> lessonProgresses = lessonProgressRepository.findAll(criteria.getSpecification(), pageable);
    List<LessonProgressDto> lessonProgressDtos = lessonProgressMapper.fromEntityToLessonProgressDtoList(lessonProgresses.getContent());
    responseListDto.setContent(lessonProgressDtos);
    responseListDto.setTotalElements(lessonProgresses.getTotalElements());
    responseListDto.setTotalPages(lessonProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson progress success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSP_ST_L')")
  public ApiMessageDto<ResponseListDto<List<LessonProgressDisplayDto>>> listByStudent(LessonProgressCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonProgressDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonProgressDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<LessonProgress> lessonProgresses = lessonProgressRepository.findAll(criteria.getSpecification(), pageable);
    List<LessonProgressDisplayDto> lessonProgressDtos = lessonProgressMapper.fromEntityToLessonProgressDisplayDtoList(lessonProgresses.getContent());
    responseListDto.setContent(lessonProgressDtos);
    responseListDto.setTotalElements(lessonProgresses.getTotalElements());
    responseListDto.setTotalPages(lessonProgresses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson progress success");
    return apiMessageDto;
  }
}
