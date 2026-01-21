package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionEducatorDto;
import com.base.auth.dto.lessonQuestion.LessonQuestionStudentDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.lessonQuestion.CreateLessonQuestionForm;
import com.base.auth.form.lessonQuestion.UpdateLessonQuestionForm;
import com.base.auth.mapper.LessonQuestionMapper;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonQuestion;
import com.base.auth.model.criteria.LessonQuestionCriteria;
import com.base.auth.repository.LessonQuestionRepository;
import com.base.auth.repository.LessonRepository;
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
@RequestMapping("/v1/lesson_question")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class LessonQuestionController extends ABasicController{
  @Autowired
  LessonQuestionRepository lessonQuestionRepository;

  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  LessonQuestionMapper lessonQuestionMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSQ_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateLessonQuestionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(form.getLessonId()).orElseThrow(()
    -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    if (!form.getQuestionType().equals(ITDreamConstant.QUESTION_TYPE_QUIZ)){
      if (form.getOptions() != null){
        throw new BadRequestException("Cannot create options when the question type is not quiz", ErrorCode.LESSON_QUESTION_ERROR_NOT_CREATE);
      }
      Boolean existQuestion = lessonQuestionRepository.existsByQuestionAndLessonId(form.getQuestion(), form.getLessonId());
      if (existQuestion){
        throw new BadRequestException("Question already exist", ErrorCode.LESSON_QUESTION_ERROR_EXIST);
      }
    } else {
      Boolean existQuestion = lessonQuestionRepository.existsByQuestionAndOptionsAndLessonId(form.getQuestion(),form.getOptions(), form.getLessonId());
      if (existQuestion){
        throw new BadRequestException("Question already exist", ErrorCode.LESSON_QUESTION_ERROR_EXIST);
      }
    }
    LessonQuestion lessonQuestion = lessonQuestionMapper.fromCreateLessonQuestionFormToEntity(form);
    lessonQuestion.setLesson(lesson);
    lessonQuestionRepository.save(lessonQuestion);
    if (lesson.getTotalQuestion() == null || lesson.getTotalQuestion() == 0){
      lesson.setTotalQuestion(1);
      lesson.setTotalError(1);
    } else {
      lesson.setTotalQuestion(lesson.getTotalQuestion() + 1);
      lesson.setTotalError((int) Math.ceil((lesson.getTotalError() + 1) / 2));
    }
    lessonRepository.save(lesson);
    apiMessageDto.setMessage("Create lesson question success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSQ_L')")
  public ApiMessageDto<ResponseListDto<List<LessonQuestionDto>>> list(LessonQuestionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonQuestionDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonQuestionDto>> responseListDto = new ResponseListDto<>();
    Page<LessonQuestion> lessonQuestions = lessonQuestionRepository.findAll(criteria.getSpecification(), pageable);
    List<LessonQuestionDto> lessonQuestionDtos = lessonQuestionMapper.fromEntityToLessonQuestionDtoList(lessonQuestions.getContent());
    responseListDto.setContent(lessonQuestionDtos);
    responseListDto.setTotalElements(lessonQuestions.getTotalElements());
    responseListDto.setTotalPages(lessonQuestions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson question success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSQ_ED_L')")
  public ApiMessageDto<ResponseListDto<List<LessonQuestionEducatorDto>>> listByEducator(LessonQuestionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonQuestionEducatorDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonQuestionEducatorDto>> responseListDto = new ResponseListDto<>();
    Page<LessonQuestion> lessonQuestions = lessonQuestionRepository.findAll(criteria.getSpecification(), pageable);
    List<LessonQuestionEducatorDto> lessonQuestionDtos = lessonQuestionMapper.fromEntityToLessonQuestionEducatorDtoList(lessonQuestions.getContent());
    responseListDto.setContent(lessonQuestionDtos);
    responseListDto.setTotalElements(lessonQuestions.getTotalElements());
    responseListDto.setTotalPages(lessonQuestions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson question success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSQ_ST_L')")
  public ApiMessageDto<ResponseListDto<List<LessonQuestionStudentDto>>> listByStudent(LessonQuestionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonQuestionStudentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonQuestionStudentDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.COURSE_STATUS_ACTIVE);
    Page<LessonQuestion> lessonQuestions = lessonQuestionRepository.findAll(criteria.getSpecification(), pageable);
    List<LessonQuestionStudentDto> lessonQuestionDtos = lessonQuestionMapper.fromEntityToLessonQuestionStudentDtoList(lessonQuestions.getContent());
    responseListDto.setContent(lessonQuestionDtos);
    responseListDto.setTotalElements(lessonQuestions.getTotalElements());
    responseListDto.setTotalPages(lessonQuestions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson question success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSQ_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateLessonQuestionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    LessonQuestion lessonQuestion = lessonQuestionRepository.findById(form.getId()).orElseThrow(()
    -> new NotFoundException("Lesson question not found", ErrorCode.LESSON_QUESTION_ERROR_NOT_FOUND));
    if (!form.getQuestionType().equals(lessonQuestion.getQuestionType())){
      if (!form.getQuestionType().equals(ITDreamConstant.QUESTION_TYPE_QUIZ)){
        if (form.getOptions() != null){
          throw new BadRequestException("Cannot update options when the question type is not quiz", ErrorCode.LESSON_QUESTION_ERROR_NOT_UPDATE);
        }
        Boolean existQuestion = lessonQuestionRepository.existsByQuestionAndLessonId(form.getQuestion(), lessonQuestion.getLesson().getId());
        if (existQuestion) {
          throw new BadRequestException("Lesson question already exist", ErrorCode.LESSON_QUESTION_ERROR_EXIST);
        }
      } else {
        Boolean existQuestion = lessonQuestionRepository.existsByQuestionAndOptionsAndLessonId(form.getQuestion(),
            form.getOptions(), lessonQuestion.getLesson().getId());
        if (existQuestion) {
          throw new BadRequestException("Lesson question already exist", ErrorCode.LESSON_QUESTION_ERROR_EXIST);
        }
      }
    }
    lessonQuestionMapper.fromUpdateLessonQuestionFormToEntity(form, lessonQuestion);
    lessonQuestionRepository.save(lessonQuestion);
    apiMessageDto.setMessage("Update lesson question success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LSQ_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    LessonQuestion lessonQuestion = lessonQuestionRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Lesson question not found", ErrorCode.LESSON_QUESTION_ERROR_NOT_FOUND));
    Lesson lesson = lessonQuestion.getLesson();
    lesson.setTotalError((int) Math.ceil((lesson.getTotalError() - 1) / 2));
    lesson.setTotalQuestion(lesson.getTotalQuestion() - 1);
    lessonRepository.save(lesson);
    lessonQuestionRepository.delete(lessonQuestion);
    apiMessageDto.setMessage("Delete lesson question success");
    return apiMessageDto;
  }
}
