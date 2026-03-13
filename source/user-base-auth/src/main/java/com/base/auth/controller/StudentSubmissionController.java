package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.studentSubmission.StudentSubmissionDisplayDto;
import com.base.auth.mapper.StudentSubmissionMapper;
import com.base.auth.model.StudentSubmission;
import com.base.auth.model.criteria.StudentSubmissionCriteria;
import com.base.auth.repository.StudentSubmissionRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/student_submission")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StudentSubmissionController extends ABasicController{
  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  StudentSubmissionMapper studentSubmissionMapper;

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SSM_ED_CPL')")
  public ApiMessageDto<ResponseListDto<List<StudentSubmissionDisplayDto>>> listStudentSubmission(
      StudentSubmissionCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<StudentSubmissionDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<StudentSubmissionDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<StudentSubmission> studentSubmissions = studentSubmissionRepository.findAll(criteria.getSpecification(), pageable);
    List<StudentSubmissionDisplayDto> studentSubmissionDisplayDtos = studentSubmissionMapper.fromEntityToStudentSubmissionDisplayDtoList(studentSubmissions.getContent());
    responseListDto.setContent(studentSubmissionDisplayDtos);
    responseListDto.setTotalElements(studentSubmissions.getTotalElements());
    responseListDto.setTotalPages(studentSubmissions.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list student submission success");
    return apiMessageDto;
  }
}
