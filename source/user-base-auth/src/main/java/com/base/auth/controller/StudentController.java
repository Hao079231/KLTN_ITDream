package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.account.OtpDto;
import com.base.auth.dto.student.ProfileStudentDto;
import com.base.auth.dto.student.StudentDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.student.SignUpStudentForm;
import com.base.auth.form.student.UpdateProfileStudentForm;
import com.base.auth.form.student.UpdateStudentForm;
import com.base.auth.mapper.AccountMapper;
import com.base.auth.mapper.StudentMapper;
import com.base.auth.model.Account;
import com.base.auth.model.Achievement;
import com.base.auth.model.Feedback;
import com.base.auth.model.Group;
import com.base.auth.model.Simulation;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.StudentCriteria;
import com.base.auth.repository.AccountRepository;
import com.base.auth.repository.AchievementRepository;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.FeedbackRepository;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.GroupRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.StudentRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.service.StudentService;
import com.base.auth.utils.AESUtils;
import com.base.auth.utils.JsonUitls;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/v1/student")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StudentController extends ABasicController{
  @Autowired
  StudentRepository studentRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  StudentMapper studentMapper;

  @Autowired
  AccountMapper accountMapper;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  StudentService studentService;

  @PostMapping(value = "/signup", produces= MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<OtpDto> create(@Valid @RequestBody SignUpStudentForm signUpStudentForm, BindingResult bindingResult)
  {
    ApiMessageDto<OtpDto> apiMessageDto = new ApiMessageDto<>();
    Boolean existUsername = accountRepository.existsByUsername(signUpStudentForm.getUsername());
    if (existUsername){
      throw new BadRequestException("Tên đăng nhập đã tồn tại", ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
    }

    Boolean existEmail = accountRepository.existsByEmail(signUpStudentForm.getEmail());
    if (existEmail)
    {
      throw new BadRequestException("Email đã tồn tại", ErrorCode.ACCOUNT_ERROR_EMAIL_EXIST);
    }

    Boolean existPhone = accountRepository.existsByPhone(signUpStudentForm.getPhone());
    if (existPhone)
    {
      throw new BadRequestException("Số điện thoại đã tồn tại", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
    }
    Account account = accountMapper.fromSignUpStudentToAccount(signUpStudentForm);
    account.setPassword(passwordEncoder.encode(signUpStudentForm.getPassword()));
    account.setKind(ITDreamConstant.USER_KIND_STUDENT);
    Group group = groupRepository.findFirstByKind(ITDreamConstant.USER_KIND_STUDENT);
    if (group == null){
      throw new NotFoundException("Không tìm thấy nhóm", ErrorCode.GROUP_ERROR_NOT_FOUND);
    }
    account.setGroup(group);
    account.setStatus(ITDreamConstant.STATUS_VERIFY);
    String otp = userBaseApiService.getRequestOTP();
    account.setAttemptCode(0);
    account.setResetPwdCode(otp);
    account.setResetPwdTime(new Date());
    accountRepository.save(account);

    Student student = new Student();
    student.setAccount(account);
    studentRepository.save(student);

    sendVerifyAccount(account);
    OtpDto otpDto = new OtpDto();
    String hash = AESUtils.encrypt (account.getId()+";"+otp, true);
    otpDto.setIdHash(hash);
    apiMessageDto.setData(otpDto);
    apiMessageDto.setMessage("Đăng ký thành công, vui lòng kiểm tra email.");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces= MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ST_L')")
  public ApiMessageDto<ResponseListDto<List<StudentDto>>> getListStudent(StudentCriteria studentCriteria , Pageable pageable)
  {
    ApiMessageDto<ResponseListDto<List<StudentDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentDto>> responseListDto = new ResponseListDto<>();
    Page<Student> listStudent = studentRepository.findAll(studentCriteria.getSpecification(),pageable);
    responseListDto.setContent(studentMapper.fromStudentListToStudentDtoList(listStudent.getContent()));
    responseListDto.setTotalElements(listStudent.getTotalElements());
    responseListDto.setTotalPages(listStudent.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách học viên thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ST_V')")
  public ApiMessageDto<StudentDto> getStudent(@PathVariable("id") Long id)
  {
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<StudentDto> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy học viên", ErrorCode.USER_ERROR_NOT_FOUND));
    apiMessageDto.setData(studentMapper.fromEntityToStudentDto(student));
    apiMessageDto.setMessage("Lấy thông tin học viên thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ST_U')")
  public ApiMessageDto<String> updateStudent(@Valid @RequestBody UpdateStudentForm updateStudentForm, BindingResult bindingResult) {
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(updateStudentForm.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy học viên", ErrorCode.USER_ERROR_NOT_FOUND));

    Account account = accountRepository.findById(student.getAccount().getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

    if (!Objects.equals(student.getAccount().getUsername(), updateStudentForm.getUsername())){
      Boolean existUsername = accountRepository.existsByUsername(updateStudentForm.getUsername());
      if (existUsername){
        throw new BadRequestException("Tên đăng nhập đã tồn tại", ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
      }
      account.setUsername(updateStudentForm.getUsername());
    }

    if (!Objects.equals(student.getAccount().getEmail(), updateStudentForm.getEmail())){
      Boolean existEmail = accountRepository.existsByEmail(updateStudentForm.getEmail());
      if (existEmail)
      {
        throw new BadRequestException("Email đã tồn tại", ErrorCode.ACCOUNT_ERROR_EMAIL_EXIST);
      }
      account.setEmail(updateStudentForm.getEmail());
    }

    if (!Objects.equals(student.getAccount().getPhone(), updateStudentForm.getPhone())){
      Boolean existPhone = accountRepository.existsByPhone(updateStudentForm.getPhone());
      if (existPhone){
        throw new BadRequestException("Số điện thoại đã tồn tại", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
      }
      account.setPhone(updateStudentForm.getPhone());
    }

    if (StringUtils.isNoneBlank(updateStudentForm.getPassword())){
      account.setPassword(passwordEncoder.encode(updateStudentForm.getPassword()));
    }

    if (StringUtils.isNotBlank(updateStudentForm.getAvatarPath())) {
      if (!updateStudentForm.getAvatarPath().equals(account.getAvatarPath())){
        userBaseApiService.deleteByFilePath(account.getAvatarPath());
        account.setAvatarPath(updateStudentForm.getAvatarPath());
      }
    }
    accountMapper.fromUpdateStudentFormToEntity(updateStudentForm, account);
    accountRepository.save(account);
    apiMessageDto.setMessage("Cập nhật học viên thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ST_D')")
  public ApiMessageDto<String> deleteStudent(@PathVariable("id") Long id)
  {
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy học viên", ErrorCode.USER_ERROR_NOT_FOUND));

    Account account = accountRepository.findById(student.getAccount().getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

    if (Objects.equals(account.getKind(), ITDreamConstant.USER_KIND_ADMIN)){
      throw new BadRequestException("Không được phép xóa quản trị viên", ErrorCode.ACCOUNT_ERROR_NOT_ALLOW_DELETE_ADMIN);
    }
    studentService.deleteByStudent(student);
    studentRepository.delete(student);
    accountRepository.delete(account);
    apiMessageDto.setMessage("Xóa học viên thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ST_U_P')")
  public ApiMessageDto<ProfileStudentDto> getProfileForStudent(){
    ApiMessageDto<ProfileStudentDto> apiMessageDto = new ApiMessageDto<>();
    Account account = accountRepository.findById(getCurrentUser()).orElseThrow(
        () -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Student student = studentRepository.findById(account.getId()).orElseThrow(
        () -> new NotFoundException("Không tìm thấy học viên", ErrorCode.USER_ERROR_NOT_FOUND));
    ProfileStudentDto studentDto = studentMapper.fromStudentToProfileDto(student);
    apiMessageDto.setData(studentDto);
    apiMessageDto.setMessage("Lấy hồ sơ học viên thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/client_update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ST_U_U')")
  public ApiMessageDto<String> updateProfileForStudent(@Valid @RequestBody UpdateProfileStudentForm updateStudentForm, BindingResult bindingResult) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account currentAccount = accountRepository.findById(getCurrentUser()).orElseThrow(() ->
        new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Student currentUser = studentRepository.findById(currentAccount.getId()).orElseThrow(() ->
        new NotFoundException("Không tìm thấy học viên", ErrorCode.USER_ERROR_NOT_FOUND));

    if (!Objects.equals(currentAccount.getUsername(), updateStudentForm.getUsername())) {
      Boolean existUsername = accountRepository.existsByUsername(updateStudentForm.getUsername());
      if (existUsername){
        throw new BadRequestException("Tên đăng nhập đã tồn tại", ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
      }
      currentAccount.setUsername(updateStudentForm.getUsername());
    }

    if (!Objects.equals(currentAccount.getPhone(), updateStudentForm.getPhone())){
      Boolean existPhone = accountRepository.existsByPhone(updateStudentForm.getPhone());
      if (existPhone){
        throw new BadRequestException("Số điện thoại đã tồn tại", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
      }
      currentAccount.setPhone(updateStudentForm.getPhone());
    }

    if (StringUtils.isNotBlank(updateStudentForm.getAvatarPath())) {
      if (!updateStudentForm.getAvatarPath().equals(currentAccount.getAvatarPath())){
        userBaseApiService.deleteByFilePath(currentAccount.getAvatarPath());
        currentAccount.setAvatarPath(updateStudentForm.getAvatarPath());
      }
    }

    if (updateStudentForm.getPreferences() != null){
      currentUser.setPreferences(JsonUitls.convertObjectToString(updateStudentForm.getPreferences()));
    }

    accountMapper.fromUpdateProfileStudentFormToEntity(updateStudentForm, currentAccount);
    currentUser.setAccount(currentAccount);
    accountRepository.save(currentAccount);
    studentRepository.save(currentUser);
    apiMessageDto.setMessage("Cập nhật hồ sơ học viên thành công");
    return apiMessageDto;
  }
}