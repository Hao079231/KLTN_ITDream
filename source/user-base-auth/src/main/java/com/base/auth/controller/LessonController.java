package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.lesson.LessonDisplayDto;
import com.base.auth.dto.lesson.LessonDto;
import com.base.auth.dto.lesson.LessonEducatorDto;
import com.base.auth.dto.lesson.LessonStudentDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.RequestProcessVideoMessageForm;
import com.base.auth.form.lesson.CreateLessonForm;
import com.base.auth.form.lesson.UpdateLessonForm;
import com.base.auth.mapper.LessonMapper;
import com.base.auth.model.Chapter;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.repository.ChapterRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.LessonQuestionRepository;
import com.base.auth.repository.LessonRepository;
import com.base.auth.service.LessonService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/lesson")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class LessonController extends ABasicController{
  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  ChapterRepository chapterRepository;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  LessonQuestionRepository lessonQuestionRepository;

  @Autowired
  LessonMapper lessonMapper;

  @Autowired
  ProcessVideoService processVideoService;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Autowired
  LessonService lessonService;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateLessonForm form, BindingResult bindingResult){
    if(!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Chapter chapter = chapterRepository.findById(form.getChapterId()).orElseThrow(()
    -> new NotFoundException("Chapter not found", ErrorCode.CHAPTER_ERROR_NOT_FOUND));
    Boolean existLesson = lessonRepository.existsByChapterIdAndTitle(form.getChapterId(), form.getTitle());
    if (existLesson){
      throw new BadRequestException("Lesson title already exist", ErrorCode.LESSON_ERROR_EXIST);
    }

    Lesson previous = null;
    Lesson next = null;
    if (form.getPreviousId() != null){
      previous = lessonRepository.findById(form.getPreviousId()).orElseThrow(()
          -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    }

    if (form.getNextId() != null){
      next = lessonRepository.findById(form.getNextId()).orElseThrow(()
          -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    }

    if (previous != null && !Objects.equals(previous.getChapter().getId(), chapter.getId())){
      throw new BadRequestException("Previous lesson not in same chapter", ErrorCode.LESSON_ERROR_SAME_CHAPTER);
    }

    if (next != null && !Objects.equals(next.getChapter().getId(), chapter.getId())){
      throw new BadRequestException("Next lesson not in same chapter", ErrorCode.LESSON_ERROR_SAME_CHAPTER);
    }

    // Kiểm tra trường hợp khi mà thêm lesson vào giữa
    if (previous != null && next != null){
      if (previous.getNext() == null || !Objects.equals(previous.getNext().getId(), next.getId())){
        throw new BadRequestException("Invalid lesson position", ErrorCode.LESSON_ERROR_POSITION);
      }
    }

    // Kiểm tra trường hợp khi mà thêm lesson vào đầu
    if (previous == null && next != null && next.getPrevious() != null) {
      throw new BadRequestException("Invalid head insertion", ErrorCode.LESSON_ERROR_CREATE);
    }

    // Kiểm tra trường hợp khi mà thêm lesson vào cuối
    if (next == null && previous != null && previous.getNext() != null) {
      throw new BadRequestException("Invalid tail insertion", ErrorCode.LESSON_ERROR_CREATE);
    }

    Lesson lesson = lessonMapper.fromCreateLessonFormToEntity(form);
    lesson.setChapter(chapter);
    lesson.setPrevious(previous);
    lesson.setNext(next);

    Course course = lesson.getChapter().getCourse();
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    lessonRepository.saveAndFlush(lesson);

    if (previous != null){
      previous.setNext(lesson);
      lessonRepository.save(previous);
    }

    if (next != null){
      next.setPrevious(lesson);
      lessonRepository.save(next);
    }

    if (StringUtils.isNotBlank(lesson.getVideoPath()) && lesson.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(lesson.getId());
      data.setUrl(lesson.getVideoPath());
      data.setKind(ITDreamConstant.KIND_LESSON);
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }
    apiMessageDto.setMessage("Create lesson success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_L')")
  public ApiMessageDto<ResponseListDto<List<LessonDto>>> list(@RequestParam("chapterId") Long chapterId, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonDto>> responseListDto = new ResponseListDto<>();
    Page<Lesson> lessons = lessonService.getLessonsByChapterOrdered(chapterId, pageable);
    responseListDto.setContent(lessonMapper.fromEntityToLessonDtoList(lessons.getContent()));
    responseListDto.setTotalElements(lessons.getTotalElements());
    responseListDto.setTotalPages(lessons.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_V')")
  public ApiMessageDto<LessonDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<LessonDto> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    apiMessageDto.setData(lessonMapper.fromEntityToLessonDto(lesson));
    apiMessageDto.setMessage("Get lesson success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_ED_L')")
  public ApiMessageDto<ResponseListDto<List<LessonDisplayDto>>> listByEducator(@RequestParam("chapterId") Long chapterId, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Lesson> lessons = lessonService.getLessonsByChapterOrdered(chapterId, pageable);
    responseListDto.setContent(lessonMapper.fromEntityToLessonDisplayDtoList(lessons.getContent()));
    responseListDto.setTotalElements(lessons.getTotalElements());
    responseListDto.setTotalPages(lessons.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_ED_V')")
  public ApiMessageDto<LessonEducatorDto> getByEducator(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<LessonEducatorDto> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    apiMessageDto.setData(lessonMapper.fromEntityToLessonEducatorDto(lesson));
    apiMessageDto.setMessage("Get lesson success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<LessonDisplayDto>>> listByStudent(@RequestParam("chapterId") Long chapterId, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<LessonDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<LessonDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Lesson> lessons = lessonService.getLessonsByStudent(chapterId, pageable);
    responseListDto.setContent(lessonMapper.fromEntityToLessonDisplayDtoList(lessons.getContent()));
    responseListDto.setTotalElements(lessons.getTotalElements());
    responseListDto.setTotalPages(lessons.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list lesson success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_ST_V')")
  public ApiMessageDto<LessonStudentDto> getByStudent(@PathVariable("id") Long id){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<LessonStudentDto> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    apiMessageDto.setData(lessonMapper.fromEntityToLessonStudentDto(lesson));
    apiMessageDto.setMessage("Get lesson success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateLessonForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(form.getId()).orElseThrow(()
    -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    Chapter chapter = chapterRepository.findById(form.getChapterId()).orElseThrow(()
    -> new NotFoundException("Chapter not found", ErrorCode.CHAPTER_ERROR_NOT_FOUND));
    if (!Objects.equals(lesson.getChapter().getId(), form.getChapterId())){
      Boolean existTitle = lessonRepository.existsByChapterIdAndTitle(form.getChapterId(), form.getTitle());
      if (existTitle){
        throw new BadRequestException("Lesson title already exist", ErrorCode.LESSON_ERROR_EXIST);
      }
    }
    Lesson oldPrev = lesson.getPrevious();
    Lesson oldNext = lesson.getNext();
    Lesson newPrev = null;
    Lesson newNext = null;
    if (form.getPreviousId() != null){
      newPrev = lessonRepository.findById(form.getPreviousId()).orElseThrow(()
      -> new NotFoundException("Previous lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    }
    if (form.getNextId() != null){
      newNext = lessonRepository.findById(form.getNextId()).orElseThrow(()
          -> new NotFoundException("Next lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));
    }

    // Kiểm tra xem lesson trước và sau lesson chuẩn bị cập nhật vị trí có nằm cùng chương hay không
    if (newPrev != null && !newPrev.getChapter().getId().equals(lesson.getChapter().getId()))
      throw new BadRequestException("Invalid chapter", ErrorCode.LESSON_ERROR_SAME_CHAPTER);

    if (newNext != null && !newNext.getChapter().getId().equals(lesson.getChapter().getId()))
      throw new BadRequestException("Invalid chapter", ErrorCode.LESSON_ERROR_SAME_CHAPTER);

    // Kiểm tra xem nếu lesson hiện tại được chuyển vào giữa 2 lesson thì vị trí có đúng không
    if (newPrev != null && newNext != null) {
      if (newPrev.getNext() == null || !newPrev.getNext().getId().equals(newNext.getId())) {
        throw new BadRequestException("Invalid position", ErrorCode.LESSON_ERROR_POSITION);
      }
    }

    if (StringUtils.isNotBlank(form.getImagePath()) &&
        lesson.getImagePath().toLowerCase().startsWith(File.separator + "image") &&
        !Objects.equals(form.getImagePath(), lesson.getImagePath())){
      userBaseApiService.deleteByFilePath(lesson.getImagePath());
    }

    if (StringUtils.isNotBlank(form.getFilePath()) &&
        lesson.getFilePath().toLowerCase().startsWith(File.separator + "document") &&
        !Objects.equals(form.getFilePath(), lesson.getFilePath())){
      userBaseApiService.deleteByFilePath(lesson.getFilePath());
    }

    if (StringUtils.isNotBlank(form.getVideoPath()) &&
        lesson.getVideoPath().toLowerCase().startsWith(File.separator + "video") &&
        !Objects.equals(form.getVideoPath(), lesson.getVideoPath())){
      userBaseApiService.deleteByFilePath(lesson.getVideoPath());
    }

    lessonMapper.fromUpdateLessonFormToEntity(form, lesson);
    if (chapter != null){
      lesson.setChapter(chapter);
    }

    if (StringUtils.isNotBlank(form.getVideoPath()) &&
        form.getVideoPath().toLowerCase().startsWith(File.separator + "video") &&
        Objects.equals(form.getVideoPath(), lesson.getVideoPath())){
      RequestProcessVideoMessageForm data = new RequestProcessVideoMessageForm();
      data.setId(lesson.getId());
      data.setKind(ITDreamConstant.KIND_LESSON);
      data.setUrl(form.getVideoPath());
      data.setTsSecond(tsSecond);
      processVideoService.sendProcessVideoMessage(data);
    }

    // Xóa vị trí cũ hiện tại của lesson
    if (oldPrev != null) {
      oldPrev.setNext(oldNext);
    }
    if (oldNext != null) {
      oldNext.setPrevious(oldPrev);
    }

    // Thêm vào vị trí mới
    lesson.setPrevious(newPrev);
    lesson.setNext(newNext);

    if (newPrev != null) {
      newPrev.setNext(lesson);
      lessonRepository.save(newPrev);
    }

    if (newNext != null) {
      newNext.setPrevious(lesson);
      lessonRepository.save(newNext);
    }
    Course course = lesson.getChapter().getCourse();
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    lessonRepository.save(lesson);
    apiMessageDto.setMessage("Update lesson success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('LS_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Lesson lesson = lessonRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Lesson not found", ErrorCode.LESSON_ERROR_NOT_FOUND));

    Lesson prev = lesson.getPrevious();
    Lesson next = lesson.getNext();

    if (prev != null) {
      prev.setNext(next);
      lessonRepository.save(prev);
    }

    if (next != null) {
      next.setPrevious(prev);
      lessonRepository.save(next);
    }

    if (StringUtils.isNotBlank(lesson.getImagePath()) &&
        lesson.getImagePath().toLowerCase().startsWith(File.separator + "image")){
      userBaseApiService.deleteByFilePath(lesson.getImagePath());
    }

    if (StringUtils.isNotBlank(lesson.getFilePath()) &&
        lesson.getFilePath().toLowerCase().startsWith(File.separator + "document")){
      userBaseApiService.deleteByFilePath(lesson.getFilePath());
    }

    if (StringUtils.isNotBlank(lesson.getVideoPath()) &&
        lesson.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      userBaseApiService.deleteByFilePath(lesson.getVideoPath());
    }
    lessonQuestionRepository.deleteAllByLessonId(id);
    Course course = lesson.getChapter().getCourse();
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    lessonRepository.delete(lesson);
    apiMessageDto.setMessage("Delete lesson success");
    return apiMessageDto;
  }
}
