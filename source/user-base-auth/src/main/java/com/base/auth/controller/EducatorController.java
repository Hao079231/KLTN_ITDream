package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.account.OtpDto;
import com.base.auth.dto.educator.EducatorDto;
import com.base.auth.dto.educator.ProfileEducatorDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.educator.RequestEducatorIdForm;
import com.base.auth.form.educator.SignUpEducatorForm;
import com.base.auth.form.educator.UpdateEducatorForm;
import com.base.auth.form.educator.UpdateProfileEducatorForm;
import com.base.auth.mapper.AccountMapper;
import com.base.auth.mapper.EducatorMapper;
import com.base.auth.model.Account;
import com.base.auth.model.Blog;
import com.base.auth.model.JobPost;
import com.base.auth.model.Organization;
import com.base.auth.model.Simulation;
import com.base.auth.model.Educator;
import com.base.auth.model.Group;
import com.base.auth.model.criteria.EducatorCriteria;
import com.base.auth.repository.AccountRepository;
import com.base.auth.repository.BlogRepository;
import com.base.auth.repository.EducatorRepository;
import com.base.auth.repository.GroupRepository;
import com.base.auth.repository.JobPostRepository;
import com.base.auth.repository.OrganizationRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.service.JobPostService;
import com.base.auth.service.SimulationService;
import com.base.auth.utils.AESUtils;
import java.util.Date;
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
@RequestMapping("/v1/educator")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class EducatorController extends ABasicController{
  @Autowired
  EducatorRepository educatorRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  EducatorMapper educatorMapper;

  @Autowired
  AccountMapper accountMapper;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  SimulationService simulationService;

  @Autowired
  OrganizationRepository organizationRepository;

  @Autowired
  BlogRepository blogRepository;

  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  JobPostService jobPostService;

  @PostMapping(value = "/signup", produces= MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<OtpDto> create(@Valid @RequestBody SignUpEducatorForm signUpEducatorForm, BindingResult bindingResult)
  {
    ApiMessageDto<OtpDto> apiMessageDto = new ApiMessageDto<>();
    Boolean existUsername = accountRepository.existsByUsername(signUpEducatorForm.getUsername());
    if (existUsername){
      throw new BadRequestException("Tên đăng nhập đã tồn tại", ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
    }

    Boolean existEmail = accountRepository.existsByEmail(signUpEducatorForm.getEmail());
    if (existEmail)
    {
      throw new BadRequestException("Email đã tồn tại", ErrorCode.ACCOUNT_ERROR_EMAIL_EXIST);
    }

    Boolean existPhone = accountRepository.existsByPhone(signUpEducatorForm.getPhone());
    if (existPhone)
    {
      throw new BadRequestException("Số điện thoại đã tồn tại", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
    }

    Organization organization = organizationRepository.findById(signUpEducatorForm.getOrganizationId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tổ chức", ErrorCode.ORGANIZATION_ERROR_NOT_FOUND));

    Account account = accountMapper.fromSignUpEducatorToAccount(signUpEducatorForm);
    account.setPassword(passwordEncoder.encode(signUpEducatorForm.getPassword()));
    account.setKind(ITDreamConstant.USER_KIND_EDUCATOR);
    Group group = groupRepository.findFirstByKind(ITDreamConstant.USER_KIND_EDUCATOR);
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

    Educator educator = new Educator();
    educator.setAccount(account);
    educator.setOrganization(organization);
    educatorRepository.save(educator);

    sendVerifyAccount(account);
    OtpDto otpDto = new OtpDto();
    String hash = AESUtils.encrypt (account.getId()+";"+otp, true);
    otpDto.setIdHash(hash);
    apiMessageDto.setData(otpDto);
    apiMessageDto.setMessage("Đăng ký thành công, vui lòng kiểm tra email.");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces= MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_L')")
  public ApiMessageDto<ResponseListDto<List<EducatorDto>>> getListEducator(EducatorCriteria educatorCriteria , Pageable pageable)
  {
    ApiMessageDto<ResponseListDto<List<EducatorDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<EducatorDto>> responseListDto = new ResponseListDto<>();
    Page<Educator> listEducator = educatorRepository.findAll(educatorCriteria.getSpecification(),pageable);
    responseListDto.setContent(educatorMapper.fromEducatorListToEducatorDtoList(listEducator.getContent()));
    responseListDto.setTotalElements(listEducator.getTotalElements());
    responseListDto.setTotalPages(listEducator.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_V')")
  public ApiMessageDto<EducatorDto> getEducator(@PathVariable("id") Long id)
  {
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<EducatorDto> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));
    apiMessageDto.setData(educatorMapper.fromEntityToEducatorDto(educator));
    apiMessageDto.setMessage("Lấy thông tin tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_U')")
  public ApiMessageDto<String> updateEducator(@Valid @RequestBody UpdateEducatorForm updateEducatorForm, BindingResult bindingResult) {
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(updateEducatorForm.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));

    Account account = accountRepository.findById(educator.getAccount().getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

    if (!Objects.equals(educator.getAccount().getUsername(), updateEducatorForm.getUsername())){
      Boolean existUsername = accountRepository.existsByUsername(updateEducatorForm.getUsername());
      if (existUsername){
        throw new BadRequestException("Tên đăng nhập đã tồn tại", ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
      }
      account.setUsername(updateEducatorForm.getUsername());
    }

    if (!Objects.equals(educator.getAccount().getEmail(), updateEducatorForm.getEmail()))
    {
      Boolean existEmail = accountRepository.existsByEmail(updateEducatorForm.getEmail());
      if(existEmail)
      {
        throw new BadRequestException("Email đã tồn tại", ErrorCode.ACCOUNT_ERROR_EMAIL_EXIST);
      }
      account.setEmail(updateEducatorForm.getEmail());
    }

    if (!Objects.equals(educator.getAccount().getPhone(), updateEducatorForm.getPhone()))
    {
      Boolean existPhone = accountRepository.existsByPhone(updateEducatorForm.getPhone());
      if(existPhone)
      {
        throw new BadRequestException("Số điện thoại đã tồn tại", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
      }
      account.setPhone(updateEducatorForm.getPhone());
    }

    if(StringUtils.isNoneBlank(updateEducatorForm.getPassword()))
    {
      account.setPassword(passwordEncoder.encode(updateEducatorForm.getPassword()));
    }

    if (StringUtils.isNotBlank(updateEducatorForm.getAvatarPath())) {
      if (!updateEducatorForm.getAvatarPath().equals(account.getAvatarPath())){
        userBaseApiService.deleteByFilePath(account.getAvatarPath());
        account.setAvatarPath(updateEducatorForm.getAvatarPath());
      }
    }
    accountMapper.fromUpdateEducatorFormToEntity(updateEducatorForm, account);
    accountRepository.save(account);
    apiMessageDto.setMessage("Cập nhật tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}")
  @PreAuthorize("hasRole('ED_D')")
  public ApiMessageDto<String> deleteEducator(@PathVariable("id") Long id)
  {
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(id).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));

    Account account = educator.getAccount();
    if (account == null){
      throw new BadRequestException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
    }

    if (Objects.equals(account.getKind(), ITDreamConstant.USER_KIND_ADMIN)){
      throw new BadRequestException("Không được phép xóa quản trị viên", ErrorCode.ACCOUNT_ERROR_NOT_ALLOW_DELETE_ADMIN);
    }

    List<Simulation> simulations = simulationRepository.findAllByEducatorId(id);
    for (Simulation simulation : simulations){
      simulationService.deleteAllBySimulation(simulation);
    }

    List<JobPost> jobPosts = jobPostRepository.findAllByEducatorId(id);
    for (JobPost jobPost : jobPosts) {
      jobPostService.deleteAllByJobPost(jobPost);
    }

    List<Blog> blogs = blogRepository.findAllByEducatorId(id);
    for (Blog blog : blogs){
      if (!blog.getImage().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
        userBaseApiService.deleteByFilePath(blog.getImage());
      }
      List<Blog> subjects = blogRepository.findAllByParentId(blog.getId());
      for (Blog subject : subjects){
        if (!subject.getImage().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
          userBaseApiService.deleteByFilePath(blog.getImage());
        }
        blogRepository.delete(subject);
      }
      blogRepository.delete(blog);
    }

    if (StringUtils.isNotBlank(educator.getAccount().getAvatarPath())){
      userBaseApiService.deleteByFilePath(educator.getAccount().getAvatarPath());
    }
    simulationRepository.deleteAllByEducatorId(id);
    educatorRepository.delete(educator);
    accountRepository.delete(account);
    apiMessageDto.setMessage("Xóa tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_U_P')")
  public ApiMessageDto<ProfileEducatorDto> getProfileForEducator(){
    ApiMessageDto<ProfileEducatorDto> apiMessageDto = new ApiMessageDto<>();
    Account account = accountRepository.findById(getCurrentUser()).orElseThrow(
        () -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Educator educator = educatorRepository.findById(account.getId()).orElseThrow(
        () -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));
    ProfileEducatorDto educatorDto = educatorMapper.fromEducatorToProfileDto(educator);
    apiMessageDto.setData(educatorDto);
    apiMessageDto.setMessage("Lấy hồ sơ tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/client_update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_U_U')")
  public ApiMessageDto<String> updateProfileForEducator(@Valid @RequestBody UpdateProfileEducatorForm updateEducatorForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account currentAccount = accountRepository.findById(getCurrentUser()).orElseThrow(() ->
        new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Educator currentUser = educatorRepository.findById(currentAccount.getId()).orElseThrow(() ->
        new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));

    if (!Objects.equals(currentAccount.getUsername(), updateEducatorForm.getUsername())){
      Boolean existUsername = accountRepository.existsByUsername(updateEducatorForm.getUsername());
      if (existUsername){
        throw new BadRequestException("Tên đăng nhập đã tồn tại", ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
      }
      currentAccount.setUsername(updateEducatorForm.getUsername());
    }

    if (StringUtils.isNotBlank(updateEducatorForm.getPhone()) && !Objects.equals(currentAccount.getPhone(), updateEducatorForm.getPhone())){
      Boolean existPhone = accountRepository.existsByPhone(updateEducatorForm.getPhone());
      if (existPhone){
        throw new BadRequestException("Số điện thoại đã tồn tại", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
      }
      currentAccount.setPhone(updateEducatorForm.getPhone());
    }

    if (StringUtils.isNotBlank(updateEducatorForm.getAvatarPath())) {
      if (!updateEducatorForm.getAvatarPath().equals(currentAccount.getAvatarPath())){
        userBaseApiService.deleteByFilePath(currentAccount.getAvatarPath());
        currentAccount.setAvatarPath(updateEducatorForm.getAvatarPath());
      }
    }

    accountMapper.fromUpdateProfileEducatorFormToEntity(updateEducatorForm, currentAccount);
    currentUser.setAccount(currentAccount);
    accountRepository.save(currentAccount);
    educatorRepository.save(currentUser);
    apiMessageDto.setMessage("Cập nhật hồ sơ tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_AP')")
  public ApiMessageDto<String> approveAccountEducator(@Valid @RequestBody RequestEducatorIdForm requestEducatorIdForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(requestEducatorIdForm.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));

    Account account = accountRepository.findById(educator.getAccount().getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

    if (!Objects.equals(ITDreamConstant.STATUS_WAITING_APPROVE, account.getStatus())){
      throw new BadRequestException("Không thể duyệt tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_APPROVE);
    }
    account.setStatus(ITDreamConstant.STATUS_ACTIVE);
    accountRepository.save(account);
    apiMessageDto.setMessage("Duyệt tài khoản giảng viên thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ED_RJ')")
  public ApiMessageDto<String> rejectAccountEducator(@Valid @RequestBody RequestEducatorIdForm requestEducatorIdForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải là quản trị viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(requestEducatorIdForm.getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));

    Account account = accountRepository.findById(educator.getAccount().getId()).orElseThrow(()
        -> new NotFoundException("Không tìm thấy tài khoản", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

    if (!Objects.equals(ITDreamConstant.STATUS_WAITING_APPROVE, account.getStatus())){
      throw new BadRequestException("Không thể từ chối tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_REJECT);
    }
    account.setStatus(ITDreamConstant.STATUS_REJECT);
    accountRepository.save(account);
    apiMessageDto.setMessage("Từ chối tài khoản giảng viên thành công");
    return apiMessageDto;
  }
}