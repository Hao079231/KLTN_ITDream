package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.jobPost.JobPostAdminDto;
import com.base.auth.dto.jobPost.JobPostDto;
import com.base.auth.dto.jobPost.ListSavedJobDto;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.jobPost.CreateJobPostForm;
import com.base.auth.form.jobPost.JobPostStatusForm;
import com.base.auth.form.jobPost.StudentSavedJostPostForm;
import com.base.auth.form.jobPost.UpdateJobPostForm;
import com.base.auth.mapper.JobPostMapper;
import com.base.auth.model.Educator;
import com.base.auth.model.JobPost;
import com.base.auth.model.Nation;
import com.base.auth.model.Simulation;
import com.base.auth.model.Student;
import com.base.auth.model.criteria.JobPostCriteria;
import com.base.auth.repository.JobPostRepository;
import com.base.auth.repository.NationRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.service.UserBaseApiService;
import com.base.auth.utils.JsonUtils;
import java.util.ArrayList;
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
@RequestMapping("/v1/job-post")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class JobPostController extends ABasicController{
  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  NationRepository nationRepository;

  @Autowired
  JobPostMapper jobPostMapper;

  @Autowired
  UserBaseApiService userBaseApiService;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ED_C')")
  ApiMessageDto<String> create(@Valid @RequestBody CreateJobPostForm form,  BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Educator educator = educatorRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản giảng viên", ErrorCode.USER_ERROR_NOT_FOUND));
    JobPost jobPost = jobPostMapper.fromCreateJobPostFormToEntity(form);
    jobPost.setEducator(educator);

    if (form.getProvinceId() != null){
      Nation province = nationRepository.findById(form.getProvinceId())
          .orElseThrow(() -> new NotFoundException("Địa chỉ tỉnh / thành phố không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
      jobPost.setProvince(province);
    }

    if (form.getWardId() != null){
      Nation ward = nationRepository.findById(form.getWardId())
          .orElseThrow(() -> new NotFoundException("Địa chỉ xã / phường không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
      jobPost.setWard(ward);
    }

    List<Simulation> simulations = new ArrayList<>();
    for (Long simulationId : form.getSimulationIds()){
      Simulation simulation = simulationRepository.findById(simulationId).orElse(null);
      if (simulation != null){
        simulations.add(simulation);
      }
    }
    jobPost.setSimulations(simulations);

    if (form.getType().equals(ITDreamConstant.JOB_POST_TYPE_EVENT)){
      if (form.getDate() == null){
        throw new BadRequestException("Đăng tin sự kiện phải có ngày tổ chức", ErrorCode.JOB_POST_ERROR_DATE_NULL);
      }
      jobPost.setDate(form.getDate());
      jobPost.setEndDate(null);
    } else if (form.getType().equals(ITDreamConstant.JOB_POST_TYPE_JOB)){
      if (form.getEndDate() == null){
        throw new BadRequestException("Đăng tin tuyển dụng phải có ngày kết thúc", ErrorCode.JOB_POST_ERROR_DATE_NULL);
      }
      jobPost.setDate(null);
      jobPost.setEndDate(form.getEndDate());
    } else {
      jobPost.setDate(null);
      jobPost.setEndDate(null);
    }

    jobPostRepository.save(jobPost);
    apiMessageDto.setMessage("Tạo tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_L')")
  public ApiMessageDto<ResponseListDto<List<JobPostAdminDto>>> list(JobPostCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<JobPostAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<JobPostAdminDto>> responseListDto = new ResponseListDto<>();
    Page<JobPost> jobPosts = jobPostRepository.findAll(criteria.getSpecification(), pageable);
    List<JobPostAdminDto> jobPostAdminDtos = jobPostMapper.fromEntityToJobPostAdminDtoList(jobPosts.getContent());
    responseListDto.setContent(jobPostAdminDtos);
    responseListDto.setTotalElements(jobPosts.getTotalElements());
    responseListDto.setTotalPages(jobPosts.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator-list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ED_L')")
  public ApiMessageDto<ResponseListDto<List<JobPostDto>>> listByEducator(JobPostCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<JobPostDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<JobPostDto>> responseListDto = new ResponseListDto<>();
    criteria.setEducatorId(getCurrentUser());
    Page<JobPost> jobPosts = jobPostRepository.findAll(criteria.getSpecification(), pageable);
    List<JobPostDto> jobPostDtos = jobPostMapper.fromEntityToJobPostDtoList(jobPosts.getContent());
    responseListDto.setContent(jobPostDtos);
    responseListDto.setTotalElements(jobPosts.getTotalElements());
    responseListDto.setTotalPages(jobPosts.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<JobPostDto>>> listByClient(JobPostCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<JobPostDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<JobPostDto>> responseListDto = new ResponseListDto<>();
    criteria.setStatus(ITDreamConstant.JOB_POST_STATUS_ACTIVE);
    Page<JobPost> jobPosts = jobPostRepository.findAll(criteria.getSpecification(), pageable);
    List<JobPostDto> jobPostDtos = jobPostMapper.fromEntityToJobPostDtoList(jobPosts.getContent());
    responseListDto.setContent(jobPostDtos);
    responseListDto.setTotalElements(jobPosts.getTotalElements());
    responseListDto.setTotalPages(jobPosts.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_V')")
  public ApiMessageDto<JobPostAdminDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("Người dùng không phải quản trị viên");
    }
    ApiMessageDto<JobPostAdminDto> apiMessageDto = new ApiMessageDto<>();
    JobPost jobPost = jobPostRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tin tuyển dụng không tồn tại", ErrorCode.JOB_POST_ERROR_NOT_FOUND));
    JobPostAdminDto jobPostAdminDto = jobPostMapper.fromEntityToJobPostAdminDto(jobPost);
    apiMessageDto.setData(jobPostAdminDto);
    apiMessageDto.setMessage("Lấy chi tiết tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/educator-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ED_V')")
  public ApiMessageDto<JobPostDto> getByEducator(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải giảng viên");
    }
    ApiMessageDto<JobPostDto> apiMessageDto = new ApiMessageDto<>();
    JobPost jobPost = jobPostRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tin tuyển dụng không tồn tại", ErrorCode.JOB_POST_ERROR_NOT_FOUND));
    JobPostDto jobPostDto = jobPostMapper.fromEntityToJobPostDto(jobPost);
    apiMessageDto.setData(jobPostDto);
    apiMessageDto.setMessage("Lấy chi tiết tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/guest-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<JobPostDto> getByClient(@PathVariable("id") Long id){
    ApiMessageDto<JobPostDto> apiMessageDto = new ApiMessageDto<>();
    JobPost jobPost = jobPostRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tin tuyển dụng không tồn tại", ErrorCode.JOB_POST_ERROR_NOT_FOUND));
    JobPostDto jobPostDto = jobPostMapper.fromEntityToJobPostDto(jobPost);
    apiMessageDto.setData(jobPostDto);
    apiMessageDto.setMessage("Lấy chi tiết tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ED_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateJobPostForm form, BindingResult bindingResult){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    JobPost jobPost = jobPostRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Tin tuyển dụng không tồn tại"));
    if (StringUtils.isNotBlank(jobPost.getImage())
        && StringUtils.isNotBlank(form.getImage())
        && Objects.equals(form.getImage(), jobPost.getImage())
        && !jobPost.getImage().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(jobPost.getImage());
    }

    jobPostMapper.fromUpdateJobPostFormToEntity(form, jobPost);

    if (form.getProvinceId() != null){
      Nation province = nationRepository.findById(form.getProvinceId())
          .orElseThrow(() -> new NotFoundException("Địa chỉ tỉnh / thành phố không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
      jobPost.setProvince(province);
    }

    if (form.getWardId() != null){
      Nation ward = nationRepository.findById(form.getWardId())
          .orElseThrow(() -> new NotFoundException("Địa chỉ xã / phường không tồn tại", ErrorCode.NATION_ERROR_NOT_FOUND));
      jobPost.setWard(ward);
    }

    List<Simulation> simulations = new ArrayList<>();
    for (Long simulationId : form.getSimulationIds()){
      Simulation simulation = simulationRepository.findById(simulationId).orElse(null);
      if (simulation != null){
        simulations.add(simulation);
      }
    }
    jobPost.setSimulations(simulations);

    if (form.getType().equals(ITDreamConstant.JOB_POST_TYPE_EVENT)){
      if (form.getDate() == null){
        throw new BadRequestException("Đăng tin sự kiện phải có ngày tổ chức", ErrorCode.JOB_POST_ERROR_DATE_NULL);
      }
      jobPost.setDate(form.getDate());
      jobPost.setEndDate(null);
    } else if (form.getType().equals(ITDreamConstant.JOB_POST_TYPE_JOB)){
      if (form.getEndDate() == null){
        throw new BadRequestException("Đăng tin tuyển dụng phải có ngày kết thúc", ErrorCode.JOB_POST_ERROR_DATE_NULL);
      }
      jobPost.setDate(null);
      jobPost.setEndDate(form.getEndDate());
    } else {
      jobPost.setDate(null);
      jobPost.setEndDate(null);
    }
    jobPostRepository.save(jobPost);
    apiMessageDto.setMessage("Cập nhật tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update-status", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_UST') or hasRole('JP_ED_U')")
  public ApiMessageDto<String> updateStatus(@Valid @RequestBody JobPostStatusForm form, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    JobPost jobPost = jobPostRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Tin tuyển dụng không tồn tại", ErrorCode.JOB_POST_ERROR_NOT_FOUND));

    if (isAdmin()){
      jobPost.setStatus(form.getStatus());
      jobPost.setNotice(form.getNotice());
    } else if (isEducator()){
      if (jobPost.getEducator() == null || !Objects.equals(jobPost.getEducator().getId(), getCurrentUser())){
        throw new UnauthorizationException("Không được phép cập nhật trạng thái cơ hội việc làm này");
      }
      if (form.getStatus() != ITDreamConstant.STATUS_ACTIVE && form.getStatus() != 0){
        throw new BadRequestException("Trạng thái không hợp lệ dành cho giảng viên");
      }
      if (jobPost.getStatus() != ITDreamConstant.STATUS_ACTIVE && jobPost.getStatus() != 0){
        throw new BadRequestException("Không thể thay đổi trạng thái hiện tại của cơ hội việc làm này");
      }
      jobPost.setStatus(form.getStatus());
      jobPost.setNotice(null);
    } else {
      throw new UnauthorizationException("Người dùng không có quyền thực hiện thao tác này");
    }

    jobPostRepository.save(jobPost);
    apiMessageDto.setMessage("Cập nhật trạng thái tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ED_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isEducator()){
      throw new UnauthorizationException("Người dùng không phải là giảng viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    JobPost jobPost = jobPostRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tin tuyển dụng không tồn tại", ErrorCode.JOB_POST_ERROR_NOT_FOUND));
    if (StringUtils.isNotBlank(jobPost.getImage()) && !jobPost.getImage().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(jobPost.getImage());
    }
    jobPostRepository.deleteSimulationJobByJobId(id);
    jobPostRepository.delete(jobPost);
    apiMessageDto.setMessage("Xóa tin tuyển dụng thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/save-job", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ST_SJ')")
  public ApiMessageDto<String> saveJob(@Valid @RequestBody StudentSavedJostPostForm form,  BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("Người dùng không phải là học viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Học viên không tồn tại", ErrorCode.USER_ERROR_NOT_FOUND));
    List<Long> jobPostIds = new ArrayList<>();
    for (Long jobPostId : form.getJobPostIds()){
      JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
      if (jobPost != null && jobPost.getStatus() == ITDreamConstant.JOB_POST_STATUS_ACTIVE){
        jobPostIds.add(jobPostId);
      }
    }
    student.setSaveJobs(JsonUtils.convertObjectToString(jobPostIds));
    studentRepository.save(student);
    apiMessageDto.setMessage("Thêm tin tuyển dụng vào mục quan tâm thành công");
    return apiMessageDto;
  }

  @GetMapping(value = "/list-save-job", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('JP_ST_LSJ')")
  public ApiMessageDto<ListSavedJobDto> getListSaveJob(){
    if (!isStudent()){
      throw new UnauthorizationException("Người dùng không phải học viên");
    }
    ApiMessageDto<ListSavedJobDto> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Học viên không tồn tại", ErrorCode.USER_ERROR_NOT_FOUND));
    ListSavedJobDto listSavedJobDto = new ListSavedJobDto();
    List<Long> savedJobIds = JsonUtils.convertJsonStringToClass(student.getSaveJobs(), Long.class);
    List<Long> activeSavedJobIds = new ArrayList<>();
    if (savedJobIds != null) {
      for (Long jobPostId : savedJobIds) {
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
        if (jobPost != null && jobPost.getStatus() == ITDreamConstant.JOB_POST_STATUS_ACTIVE) {
          activeSavedJobIds.add(jobPostId);
        }
      }
    }
    listSavedJobDto.setJobPostIds(activeSavedJobIds);
    apiMessageDto.setData(listSavedJobDto);
    apiMessageDto.setMessage("Lấy danh sách tin tuyển dụng quan tâm thành công");
    return apiMessageDto;
  }
}
