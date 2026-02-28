package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.reviewSubmission.ReviewSubmissionDisplayDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.reviewSubmission.CreateCompleteReviewSubmissionForm;
import com.base.auth.form.reviewSubmission.CreateReviewSubmissionForm;
import com.base.auth.form.reviewSubmission.UpdateReviewSubmissionForm;
import com.base.auth.mapper.ReviewSubmissionMapper;
import com.base.auth.model.Account;
import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.Course;
import com.base.auth.model.Notification;
import com.base.auth.model.ReviewSubmission;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.ReviewSubmissionCriteria;
import com.base.auth.repository.AccountRepository;
import com.base.auth.repository.CorrectAnswerRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.NotificationRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
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
@RequestMapping("/v1/review_submission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ReviewSubmissionController extends ABasicController{
  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  NotificationRepository notificationRepository;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  ReviewSubmissionMapper reviewSubmissionMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateReviewSubmissionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    CorrectAnswer correctAnswer = correctAnswerRepository.findById(form.getCorrectAnswerId())
        .orElseThrow(() -> new NotFoundException("Correct answer not found", ErrorCode.CORRECT_ANSWER_ERROR_NOT_FOUND));
    Account account = accountRepository.findAccountByUsername(form.getUsername());
    if (account == null){
      throw new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
    }

    Student student = studentRepository.findById(account.getId())
        .orElseThrow(() -> new NotFoundException("Student not found", ErrorCode.USER_ERROR_NOT_FOUND));
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findByCorrectAnswerIdAndStudentId(correctAnswer.getId(), student.getId());
    if (reviewSubmission != null){
      throw new BadRequestException("Review submission already exist", ErrorCode.REVIEW_SUBMISSION_ERROR_EXIST);
    }
    reviewSubmission.setContent(form.getContent());
    reviewSubmission.setCorrectAnswer(correctAnswer);
    reviewSubmission.setStudent(student);
    reviewSubmissionRepository.save(reviewSubmission);
    apiMessageDto.setMessage("Create review submission success");
    return apiMessageDto;
  }

  @GetMapping(value = "/students_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ST_STL')")
  public ApiMessageDto<ResponseListDto<List<ReviewSubmissionDisplayDto>>> listReviewSubmissionByStudent(ReviewSubmissionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ReviewSubmissionDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ReviewSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    criteria.setStudentId(getCurrentUser());
    Page<ReviewSubmission> reviewSubmissions = reviewSubmissionRepository.findAll(criteria.getSpecification(), pageable);
    List<ReviewSubmissionDisplayDto> studentLessonsViewDtos = reviewSubmissionMapper.fromEntityToReviewSubmissionDisplayDtoList(reviewSubmissions.getContent());
    responseListDto.setContent(studentLessonsViewDtos);
    responseListDto.setTotalElements(reviewSubmissions.getTotalElements());
    responseListDto.setTotalPages(reviewSubmissions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list review submission success");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_STL')")
  public ApiMessageDto<ResponseListDto<List<ReviewSubmissionDisplayDto>>> listReviewSubmissionByEducator(ReviewSubmissionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ReviewSubmissionDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ReviewSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<ReviewSubmission> reviewSubmissions = reviewSubmissionRepository.findAll(criteria.getSpecification(), pageable);
    List<ReviewSubmissionDisplayDto> studentLessonsViewDtos = reviewSubmissionMapper.fromEntityToReviewSubmissionDisplayDtoList(reviewSubmissions.getContent());
    responseListDto.setContent(studentLessonsViewDtos);
    responseListDto.setTotalElements(reviewSubmissions.getTotalElements());
    responseListDto.setTotalPages(reviewSubmissions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list review submission success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateReviewSubmissionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Review submission not found", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_FOUND));
    reviewSubmission.setContent(form.getContent());
    reviewSubmissionRepository.save(reviewSubmission);
    apiMessageDto.setMessage("Update review submission success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Review submission not found", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_FOUND));
    reviewSubmissionRepository.delete(reviewSubmission);
    apiMessageDto.setMessage("Delete review submission success");
    return apiMessageDto;
  }

  @PutMapping(value = "/complete_review", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_CR')")
  public ApiMessageDto<String> completeReview(@Valid @RequestBody CreateCompleteReviewSubmissionForm form, BindingResult bindingResult) {
    if (!isEducator()) {
      throw new UnauthorizationException("User is not an educator");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Course course = courseRepository.findById(form.getCourseId())
        .orElseThrow(() -> new NotFoundException("Course error not found", ErrorCode.COURSE_ERROR_NOT_FOUND));
    Account account = accountRepository.findAccountByUsername(form.getStudentUsername());
    if (account == null) {
      throw new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
    }

    Student student = studentRepository.findById(account.getId())
        .orElseThrow(() -> new NotFoundException("Student not found", ErrorCode.USER_ERROR_NOT_FOUND));

    Long totalCorrectAnswer = correctAnswerRepository.countCorrectAnswerByCourseAndStudent(form.getCourseId(), student.getId());
    Long totalReview = reviewSubmissionRepository.countReviewByCourseAndStudent(form.getCourseId(), student.getId());
    if (totalCorrectAnswer == null || totalReview == null || !totalCorrectAnswer.equals(totalReview)) {
      throw new BadRequestException("Review submission is not completed yet", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_COMPLETE);
    }

    Notification notification = new Notification();
    notification.setTitle("Bài làm của bạn đã được đánh giá");
    notification.setMessage(
        "Bài làm của bạn trong khóa học "
            + course.getTitle()
            + " đã được giảng viên đánh giá. "
            + "Vui lòng truy cập khóa học để xem chi tiết phản hồi."
    );
    notification.setRefType("COURSE_REVIEW_COMPLETED");
    notification.setRefId(course.getId());
    notification.setReceiver(account);
    notification.setReadFlag(false);
    notificationRepository.save(notification);

    apiMessageDto.setMessage("Complete review submission");
    return apiMessageDto;
  }

}
