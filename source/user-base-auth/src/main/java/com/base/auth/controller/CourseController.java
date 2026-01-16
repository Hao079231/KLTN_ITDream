package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.course.CourseClientDto;
import com.base.auth.dto.course.CourseDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.course.CreateCourseForm;
import com.base.auth.form.course.RequestCourseIdForm;
import com.base.auth.form.course.UpdateCourseForm;
import com.base.auth.form.RequestProcessVideoMessageForm;
import com.base.auth.mapper.CourseMapper;
import com.base.auth.model.Course;
import com.base.auth.model.Educator;
import com.base.auth.model.Category;
import com.base.auth.model.Lesson;
import com.base.auth.model.criteria.CourseCriteria;
import com.base.auth.repository.AchievementRepository;
import com.base.auth.repository.EducatorRepository;
import com.base.auth.repository.FeedbackRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.CategoryRepository;
import com.base.auth.repository.LessonProgressRepository;
import com.base.auth.repository.CorrectAnswerRepository;
import com.base.auth.repository.LessonQuestionRepository;
import com.base.auth.repository.LessonRepository;
import com.base.auth.service.ProcessVideoService;
import java.io.File;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/v1/course")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CourseController extends ABasicController{
  @Autowired
  CourseRepository courseRepository;

  @Autowired
  CourseMapper courseMapper;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  EducatorRepository educatorRepository;

  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  LessonQuestionRepository lessonQuestionRepository;

  @Autowired
  LessonProgressRepository lessonProgressRepository;

  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

  @Autowired
  ProcessVideoService processVideoService;

  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  AchievementRepository achievementRepository;

  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateCourseForm createCourseForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(getCurrentUser()).orElseThrow(()
        -> new NotFoundException("Educator not found"));
    if (!isEducator()){
      throw new BadRequestException("User is not an educator", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Category category = categoryRepository.findById(createCourseForm.getCategoryId()).orElseThrow(()
    -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    boolean existCourse = courseRepository.existsByTitleAndEducatorId(createCourseForm.getTitle(), getCurrentUser());
    if (existCourse){
      throw new BadRequestException("Course already exist", ErrorCode.COURSE_ERROR_EXIST);
    }
    Course course = courseMapper.fromCreateCourseFormToEntity(createCourseForm);
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    course.setCategory(category);
    course.setEducator(educator);
    if (StringUtils.isNotBlank(createCourseForm.getVideoPath()) &&
        !createCourseForm.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      course.setVideoState(ITDreamConstant.STATE_COURSE_PROCESSING);
    } else {
      course.setVideoState(ITDreamConstant.STATE_COURSE_DONE);
    }
    courseRepository.saveAndFlush(course);
    if (StringUtils.isNotBlank(course.getVideoPath()) && course.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(course.getId());
      data.setKind(ITDreamConstant.KIND_COURSE);
      data.setUrl(createCourseForm.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setMessage("Create course success. Please wait for approval");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_L')")
  public ApiMessageDto<ResponseListDto<List<CourseDto>>> getList(CourseCriteria courseCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CourseDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CourseDto>> responseListDto = new ResponseListDto<>();
    Page<Course> courses = courseRepository.findAll(courseCriteria.getSpecification(), pageable);
    responseListDto.setContent(courseMapper.fromEntityToCourseDtoList(courses.getContent()));
    responseListDto.setTotalElements(courses.getTotalElements());
    responseListDto.setTotalPages(courses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_V')")
  public ApiMessageDto<CourseDto> get(@PathVariable("id") Long id){
    ApiMessageDto<CourseDto> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(id).orElseThrow(() ->
        new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    CourseDto courseDto = courseMapper.fromEntityToCourseDto(course);
    apiMessageDto.setData(courseDto);
    apiMessageDto.setMessage("Get course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<CourseDisplayDto>>> getListForClient(Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CourseDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CourseDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Course> courses = courseRepository.findAllByStatus(ITDreamConstant.COURSE_STATUS_ACTIVE, pageable);
    List<CourseDisplayDto> courseDtos = courseMapper.fromEntityToCourseDisplayDtoList(courses.getContent());
    responseListDto.setContent(courseDtos);
    responseListDto.setTotalElements(courses.getTotalElements());
    responseListDto.setTotalPages(courses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_ST_L')")
  public ApiMessageDto<ResponseListDto<List<CourseDisplayDto>>> getListForStudent(Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CourseDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CourseDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Course> courses = courseRepository.findAllByStatus(ITDreamConstant.COURSE_STATUS_ACTIVE, pageable);
    List<CourseDisplayDto> courseDtos = courseMapper.fromEntityToCourseDisplayDtoList(courses.getContent());
    responseListDto.setContent(courseDtos);
    responseListDto.setTotalElements(courses.getTotalElements());
    responseListDto.setTotalPages(courses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_ED_L')")
  public ApiMessageDto<ResponseListDto<List<CourseDisplayDto>>> getListForEducator(
      CourseCriteria courseCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CourseDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CourseDisplayDto>> responseListDto = new ResponseListDto<>();
    courseCriteria.setEducatorId(getCurrentUser());
    Page<Course> courses = courseRepository.findAll(courseCriteria.getSpecification(), pageable);
    responseListDto.setContent(courseMapper.fromEntityToCourseDisplayDtoList(courses.getContent()));
    responseListDto.setTotalElements(courses.getTotalElements());
    responseListDto.setTotalPages(courses.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<CourseClientDto> getCourseForClient(@PathVariable("id") Long id){
    ApiMessageDto<CourseClientDto> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    CourseClientDto courseDto = courseMapper.fromEntityToCourseClientDto(course);
    apiMessageDto.setData(courseDto);
    apiMessageDto.setMessage("Get course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_ST_V')")
  public ApiMessageDto<CourseClientDto> getCourseForStudent(@PathVariable("id") Long id){
    ApiMessageDto<CourseClientDto> apiMessageDto = new ApiMessageDto<>();
    if (!isStudent()){
      throw new BadRequestException("User is not a student", ErrorCode.USER_ERROR_NOT_STUDENT);
    }
    Course course = courseRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    CourseClientDto courseDto = courseMapper.fromEntityToCourseClientDto(course);
    apiMessageDto.setData(courseDto);
    apiMessageDto.setMessage("Get course success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_ED_V')")
  public ApiMessageDto<CourseClientDto> getCourseForEducator(@PathVariable("id") Long id){
    ApiMessageDto<CourseClientDto> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("User is not an educator", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Course course = courseRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(course.getEducator().getId(), getCurrentUser())){
      throw new BadRequestException("Course cannot be read", ErrorCode.COURSE_ERROR_NOT_AUTHORIZED);
    }
    CourseClientDto courseDto = courseMapper.fromEntityToCourseClientDto(course);
    apiMessageDto.setData(courseDto);
    apiMessageDto.setMessage("Get course success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateCourseForm updateCourseForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("User is not an educator");
    }
    Course course = courseRepository.findById(updateCourseForm.getId()).orElseThrow(()
    -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(course.getEducator().getId(), getCurrentUser())){
      throw new BadRequestException("Course cannot be updated", ErrorCode.COURSE_ERROR_NOT_AUTHORIZED);
    }
    if (updateCourseForm.getCategoryId() != null && !Objects.equals(course.getCategory().getId(), updateCourseForm.getCategoryId())){
      Category category = categoryRepository.findById(updateCourseForm.getCategoryId()).orElseThrow(()
          -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      course.setCategory(category);
    }

    courseMapper.fromUpdateCourseFormToEntity(updateCourseForm, course);
    if (StringUtils.isNotBlank(updateCourseForm.getVideoPath()) && updateCourseForm.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      if (StringUtils.isNotBlank(course.getVideoPath())){
        if (!Objects.equals(course.getVideoPath(), updateCourseForm.getVideoPath())){
          if (course.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
            userBaseApiService.deleteByFilePath(course.getVideoPath());
          }
          RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
          data.setId(course.getId());
          data.setKind(ITDreamConstant.KIND_COURSE);
          data.setUrl(updateCourseForm.getVideoPath());
          data.setTsSecond(tsSecond);
          processVideoService.sendProcessVideoMessage(data);
        }
      } else {
        RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
        data.setId(course.getId());
        data.setKind(ITDreamConstant.KIND_COURSE);
        data.setUrl(updateCourseForm.getVideoPath());
        data.setTsSecond(tsSecond);
        processVideoService.sendProcessVideoMessage(data);
      }
    }

    if (StringUtils.isNotBlank(updateCourseForm.getThumbnail())){
      if (StringUtils.isNotBlank(course.getThumbnail()) && !Objects.equals(course.getThumbnail(), updateCourseForm.getThumbnail())){
        userBaseApiService.deleteByFilePath(course.getThumbnail());
      }
      course.setThumbnail(updateCourseForm.getThumbnail());
    }

    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    apiMessageDto.setMessage("Update success. Please wait for approval");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/approve_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_APD')")
  @Transactional
  public ApiMessageDto<String> approveDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE_DELETE, course.getStatus())){
      throw new BadRequestException("Course cannot be deleted", ErrorCode.COURSE_ERROR_NOT_DELETE);
    }
//    List<Lesson> lessons = lessonRepository.findAllByCourseId(id);
//    for (Lesson lesson : lessons){
//      deleteLessonFiles(lesson);
//    }
    userBaseApiService.deleteByFilePath(course.getThumbnail());
    userBaseApiService.deleteByFilePath(course.getVideoPath());
//    correctAnswerRepository.deleteAllByCourseId(id);
//    lessonProgressRepository.deleteAllByCourseId(id);
//    lessonQuestionRepository.deleteAllByCourseId(id);
//    lessonRepository.deleteAllSubTaskByCourseId(id);
//    lessonRepository.deleteAllTaskByCourseId(id);
//    feedbackRepository.deleteByCourseId(id);
//    reviewSubmissionRepository.deleteByCourseId(id);
//    achievementRepository.setNullCourseId(id);
    courseRepository.delete(course);
    apiMessageDto.setMessage("Approve delete course success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/reject_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_RJD')")
  public ApiMessageDto<String> rejectDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE_DELETE, course.getStatus())){
      throw new BadRequestException("Course cannot be deleted", ErrorCode.COURSE_ERROR_NOT_DELETE);
    }
    course.setStatus(ITDreamConstant.STATUS_ACTIVE);
    courseRepository.save(course);
    apiMessageDto.setMessage("Reject delete course success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/educator_request_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_ED_RED')")
  public ApiMessageDto<String> requestDelete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new BadRequestException("User is not an educator", ErrorCode.USER_ERROR_NOT_EDUCATOR);
    }
    Course course = courseRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.STATUS_ACTIVE, course.getStatus())){
      throw new BadRequestException("Request for deletion is currently being approved", ErrorCode.COURSE_ERROR_APPROVE);
    }
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE_DELETE);
    courseRepository.save(course);
    apiMessageDto.setMessage("Request delete course success");
    return apiMessageDto;
  }

  @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_AP')")
  public ApiMessageDto<String> approve(@Valid @RequestBody RequestCourseIdForm requestCourseIdForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(requestCourseIdForm.getId()).orElseThrow(()
    -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE, course.getStatus())){
      throw new BadRequestException("Course cannot approve", ErrorCode.COURSE_ERROR_APPROVE);
    }
    course.setStatus(ITDreamConstant.STATUS_ACTIVE);
    course.setNotice(null);
    courseRepository.save(course);
    apiMessageDto.setMessage("Approve course success");
    return apiMessageDto;
  }

  @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_RJ')")
  public ApiMessageDto<String> reject(@Valid @RequestBody RequestCourseIdForm requestCourseIdForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(requestCourseIdForm.getId()).orElseThrow(()
        -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE, course.getStatus())){
      throw new BadRequestException("Course cannot approve", ErrorCode.COURSE_ERROR_APPROVE);
    }
    course.setNotice(requestCourseIdForm.getNotice());
    courseRepository.save(course);
    apiMessageDto.setMessage("Reject course success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/educator_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CS_ED_D')")
  public ApiMessageDto<String> deleteByEducator(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Course not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    if (!Objects.equals(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE, course.getStatus())){
      throw new BadRequestException("Course cannot be deleted", ErrorCode.COURSE_ERROR_NOT_DELETE);
    }
//    List<Lesson> lessons = lessonRepository.findAllByCourseId(id);
//    for (Lesson lesson : lessons){
//      deleteLessonFiles(lesson);
//    }
    userBaseApiService.deleteByFilePath(course.getThumbnail());
    userBaseApiService.deleteByFilePath(course.getVideoPath());
//    lessonQuestionRepository.deleteAllByCourseId(id);
//    lessonRepository.deleteAllSubTaskByCourseId(id);
//    lessonRepository.deleteAllTaskByCourseId(id);
    courseRepository.delete(course);
    apiMessageDto.setMessage("Delete course success");
    return apiMessageDto;
  }

  private void deleteLessonFiles(Lesson lesson) {
    userBaseApiService.deleteByFilePath(lesson.getImagePath());
    userBaseApiService.deleteByFilePath(lesson.getFilePath());
    userBaseApiService.deleteByFilePath(lesson.getVideoPath());
  }
}
