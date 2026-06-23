package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
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
import com.base.auth.model.Educator;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.StudentSubmission;
import com.base.auth.model.Simulation;
import com.base.auth.model.Notification;
import com.base.auth.model.ReviewSubmission;
import com.base.auth.model.Student;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.criteria.ReviewSubmissionCriteria;
import com.base.auth.repository.AccountRepository;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.NotificationRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
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
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  NotificationRepository notificationRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  SimulationEnrollmentRepository simulationEnrollmentRepository;

  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  TaskQuestionRepository taskQuestionRepository;

  @Autowired
  ReviewSubmissionMapper reviewSubmissionMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateReviewSubmissionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là người hướng dẫn");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    StudentSubmission studentSubmission = studentSubmissionRepository.findById(form.getStudentSubmissionId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy bài làm của học viên", ErrorCode.STUDENT_SUBMISSION_ERROR_NOT_FOUND));
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findById(form.getStudentTaskProgressId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tiến độ nhiệm vụ của học viên", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    if (!studentTaskProgress.getTask().getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      throw new BadRequestException("Không thể đánh giá nhiệm vụ", ErrorCode.TASK_ERROR_KIND_INVALID);
    }

    Boolean existTaskQuestion = taskQuestionRepository.existsByTaskId(studentTaskProgress.getTask().getId());
    if (existTaskQuestion){
      throw new BadRequestException("Không yêu cầu đánh giá cho nhiệm vụ có câu hỏi", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_CREATE);
    }

    SimulationEnrollment simulationEnrollment = studentTaskProgress.getSimulationEnrollment();
    if (!Objects.equals(simulationEnrollment.getStatus(), ITDreamConstant.SIMULATION_ENROLLMENT_COMPLETED)){
      throw new BadRequestException("Chưa hoàn thành mô phỏng", ErrorCode.SIMULATION_ENROLLMENT_ERROR_NOT_COMPLETE);
    }
    Student student = simulationEnrollment.getStudent();
    boolean existReview = reviewSubmissionRepository.existsByStudentTaskProgressId(studentTaskProgress.getId());
    if (existReview){
      throw new BadRequestException("Đánh giá bài làm đã tồn tại", ErrorCode.REVIEW_SUBMISSION_ERROR_EXIST);
    }

    ReviewSubmission reviewSubmission = new ReviewSubmission();
    reviewSubmission.setContent(form.getContent());
    reviewSubmission.setStudentSubmission(studentSubmission);
    reviewSubmission.setStudent(student);
    reviewSubmissionRepository.save(reviewSubmission);
    apiMessageDto.setMessage("Tạo đánh giá bài làm thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/students_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ST_STL')")
  public ApiMessageDto<ResponseListDto<List<ReviewSubmissionDisplayDto>>> listReviewSubmissionByStudent(ReviewSubmissionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ReviewSubmissionDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ReviewSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<ReviewSubmission> reviewSubmissions = reviewSubmissionRepository.findAll(criteria.getSpecification(), pageable);
    List<ReviewSubmissionDisplayDto> studentLessonsViewDtos = reviewSubmissionMapper.fromEntityToReviewSubmissionDisplayDtoList(reviewSubmissions.getContent());
    responseListDto.setContent(studentLessonsViewDtos);
    responseListDto.setTotalElements(reviewSubmissions.getTotalElements());
    responseListDto.setTotalPages(reviewSubmissions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách đánh giá bài làm thành công");
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
    apiMessageDto.setMessage("Lấy danh sách đánh giá bài làm thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/student_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ST_STV')")
  public ApiMessageDto<ReviewSubmissionDisplayDto> studentGet(@PathVariable("id") Long id){
    if (!isStudent()){
      throw new UnauthorizationException("Người dùng không phải là học viên");
    }
    ApiMessageDto<ReviewSubmissionDisplayDto> apiMessageDto = new ApiMessageDto<>();
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy đánh giá bài làm", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_FOUND));
    ReviewSubmissionDisplayDto reviewSubmissionDisplayDto = reviewSubmissionMapper.fromEntityToReviewSubmissionDisplayDto(reviewSubmission);
    apiMessageDto.setData(reviewSubmissionDisplayDto);
    apiMessageDto.setMessage("Lấy chi tiết đánh giá bài làm thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator_get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_STV')")
  public ApiMessageDto<ReviewSubmissionDisplayDto> educatorGet(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là người hướng dẫn");
    }
    ApiMessageDto<ReviewSubmissionDisplayDto> apiMessageDto = new ApiMessageDto<>();
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy đánh giá bài làm", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_FOUND));
    ReviewSubmissionDisplayDto reviewSubmissionDisplayDto = reviewSubmissionMapper.fromEntityToReviewSubmissionDisplayDto(reviewSubmission);
    apiMessageDto.setData(reviewSubmissionDisplayDto);
    apiMessageDto.setMessage("Lấy chi tiết đánh giá bài làm thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateReviewSubmissionForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là người hướng dẫn");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy đánh giá bài làm", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_FOUND));
    reviewSubmission.setContent(form.getContent());
    reviewSubmissionRepository.save(reviewSubmission);
    apiMessageDto.setMessage("Cập nhật đánh giá bài làm thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_D')")
  @Transactional
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là người hướng dẫn");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy đánh giá bài làm", ErrorCode.REVIEW_SUBMISSION_ERROR_NOT_FOUND));

    SimulationEnrollment enrollment = reviewSubmission.getStudentSubmission().getStudentTaskProgress().getSimulationEnrollment();
    reviewSubmissionRepository.delete(reviewSubmission);
    boolean stillHasAnyReview = studentTaskProgressRepository.existsAnyReviewedTask(
        enrollment.getId(), ITDreamConstant.TASK_KIND_SUBTASK);
    if (!stillHasAnyReview) {
      enrollment.setReviewStatus(ITDreamConstant.SIMULATION_ENROLLMENT_REVIEW_STATUS_NOT_REVIEWED);
      simulationEnrollmentRepository.save(enrollment);
    }

    apiMessageDto.setMessage("Xóa đánh giá bài làm thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/complete_review", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('RS_ED_CR')")
  public ApiMessageDto<String> completeReview(@Valid @RequestBody CreateCompleteReviewSubmissionForm form, BindingResult bindingResult) {
    if (!isEducator()) {
      throw new UnauthorizationException("Người dùng không phải là người hướng dẫn");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Simulation simulation = simulationRepository.findById(form.getSimulationId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy mô phỏng", ErrorCode.SIMULATION_ERROR_NOT_FOUND));
    Educator educator = educatorRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người hướng dẫn", ErrorCode.USER_ERROR_NOT_FOUND));
    Account account = accountRepository.findAccountByUsername(form.getStudentUsername());
    if (account == null){
      throw new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
    }
    Notification notification = new Notification();
    notification.setTitle("Bài làm của bạn đã được đánh giá.");
    notification.setMessage(
        "📚 Bài mô phỏng: " + simulation.getTitle() + "\n" +
            "👨‍🏫 Người đánh giá: " + educator.getAccount().getFullName() + "\n" +
            "🏢 Tổ chức: " + educator.getOrganization().getName() + "\n\n" +
            "Vui lòng truy cập vào mô phỏng để xem chi tiết."
    );
    notification.setRefType("SIMULATION_REVIEW_COMPLETED");
    notification.setRefId(simulation.getId());
    notification.setReceiver(account);
    notification.setReadFlag(false);
    notificationRepository.save(notification);

    simulationEnrollmentRepository
        .findBySimulationIdAndStudentAccountId(simulation.getId(), account.getId())
        .ifPresent(enrollment -> {
          enrollment.setReviewStatus(ITDreamConstant.SIMULATION_ENROLLMENT_REVIEW_STATUS_REVIEWED);
          simulationEnrollmentRepository.save(enrollment);
        });

    apiMessageDto.setMessage("Hoàn thành đánh giá bài làm");
    return apiMessageDto;
  }

}