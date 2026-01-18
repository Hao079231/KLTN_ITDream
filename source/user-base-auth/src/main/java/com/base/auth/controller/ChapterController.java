package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.chapter.ChapterDisplayDto;
import com.base.auth.dto.chapter.ChapterDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.chapter.CreateChapterForm;
import com.base.auth.form.chapter.UpdateChapterForm;
import com.base.auth.mapper.ChapterMapper;
import com.base.auth.model.Chapter;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.model.criteria.ChapterCriteria;
import com.base.auth.repository.ChapterRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.LessonRepository;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/chapter")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ChapterController extends ABasicController{
  @Autowired
  ChapterRepository chapterRepository;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  ChapterMapper chapterMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CHP_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateChapterForm form,  BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    Course course = courseRepository.findById(form.getCourseId()).orElseThrow(()
    -> new NotFoundException("Course not found"));
    Boolean existName = chapterRepository.existsByNameAndCourseId(form.getName(), course.getId());
    if (existName){
      throw new BadRequestException("Chapter name already exist", ErrorCode.CHAPTER_ERROR_EXIST);
    }
    Chapter chapter = chapterMapper.fromCreateChapterFormToEntity(form);
    Integer maxOrder = chapterRepository.findMaxChapterOrderByCourseId(course.getId());
    int nextOrder = maxOrder == null ? 1 : maxOrder + 1;
    chapter.setChapterOrder(nextOrder);
    chapter.setCourse(course);
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    chapterRepository.save(chapter);
    apiMessageDto.setMessage("Create chapter success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CHP_L')")
  public ApiMessageDto<ResponseListDto<List<ChapterDto>>> list(ChapterCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ChapterDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ChapterDto>> responseListDto = new ResponseListDto<>();
    Page<Chapter> chapters = chapterRepository.findAll(criteria.getSpecification(), pageable);
    List<ChapterDto> chapterDtos = chapterMapper.fromEntityToChapterDtoList(chapters.getContent());
    responseListDto.setContent(chapterDtos);
    responseListDto.setTotalElements(chapters.getTotalElements());
    responseListDto.setTotalPages(chapters.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list chapter success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CHP_ED_L')")
  public ApiMessageDto<ResponseListDto<List<ChapterDisplayDto>>> listByEducator(ChapterCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ChapterDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ChapterDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Chapter> chapters = chapterRepository.findAll(criteria.getSpecification(), pageable);
    List<ChapterDisplayDto> chapterDtos = chapterMapper.fromEntityToChapterDisplayDtoList(chapters.getContent());
    responseListDto.setContent(chapterDtos);
    responseListDto.setTotalElements(chapters.getTotalElements());
    responseListDto.setTotalPages(chapters.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list chapter success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CHP_ED_V')")
  public ApiMessageDto<ChapterDisplayDto> getByEducator(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<ChapterDisplayDto> apiMessageDto = new ApiMessageDto<>();
    Chapter chapter = chapterRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Chapter not found", ErrorCode.CHAPTER_ERROR_NOT_FOUND));
    apiMessageDto.setData(chapterMapper.fromEntityToChapterDisplayDto(chapter));
    apiMessageDto.setMessage("Get chapter success");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<ChapterDisplayDto>>> listByStudent(
      ChapterCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ChapterDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ChapterDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.COURSE_STATUS_ACTIVE);
    Page<Chapter> chapters = chapterRepository.findAll(criteria.getSpecification() ,pageable);
    List<ChapterDisplayDto> chapterDtos = chapterMapper.fromEntityToChapterDisplayDtoList(chapters.getContent());
    responseListDto.setContent(chapterDtos);
    responseListDto.setTotalElements(chapters.getTotalElements());
    responseListDto.setTotalPages(chapters.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list chapter success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CHP_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateChapterForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Chapter chapter = chapterRepository.findById(form.getId()).orElseThrow(()
    -> new NotFoundException("Chapter not found", ErrorCode.CHAPTER_ERROR_NOT_FOUND));
    if (!Objects.equals(form.getName(), chapter.getName())){
      Boolean existName = chapterRepository.existsByName(form.getName());
      if (existName){
        throw new BadRequestException("Chapter name already exist", ErrorCode.CHAPTER_ERROR_EXIST);
      }
    }
    chapterMapper.fromUpdateChapterFormToEntity(form, chapter);
    Course course = chapter.getCourse();
    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    chapterRepository.save(chapter);
    apiMessageDto.setMessage("Update chapter success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CHP_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if(!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Chapter chapter = chapterRepository.findById(id).orElseThrow(()
    -> new NotFoundException("Chapter not found", ErrorCode.CHAPTER_ERROR_NOT_FOUND));
    Course course = chapter.getCourse();
    Lesson current = lessonRepository.findFirstByChapterIdAndPreviousIsNull(id).orElse(null);

    while (current != null) {
      Lesson next = current.getNext();
      if (next != null) {
        next.setPrevious(null);
        lessonRepository.save(next);
      }


      current.setPrevious(null);
      current.setNext(null);
      if (StringUtils.isNotBlank(current.getImagePath())
          && current.getImagePath().toLowerCase().startsWith(File.separator + "image")) {
        userBaseApiService.deleteByFilePath(current.getImagePath());
      }

      if (StringUtils.isNotBlank(current.getFilePath())
          && current.getFilePath().toLowerCase().startsWith(File.separator + "document")) {
        userBaseApiService.deleteByFilePath(current.getFilePath());
      }

      if (StringUtils.isNotBlank(current.getVideoPath())
          && current.getVideoPath().toLowerCase().startsWith(File.separator + "video")) {
        userBaseApiService.deleteByFilePath(current.getVideoPath());
      }
      lessonRepository.delete(current);
      current = next;
    }

    course.setStatus(ITDreamConstant.COURSE_STATUS_WAITING_APPROVE);
    courseRepository.save(course);
    chapterRepository.delete(chapter);
    apiMessageDto.setMessage("Delete chapter success");
    return apiMessageDto;
  }
}
