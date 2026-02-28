package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.correctAnswer.CorrectAnswerDisplayDto;
import com.base.auth.mapper.CorrectAnswerMapper;
import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.criteria.CorrectAnswerCriteria;
import com.base.auth.repository.CorrectAnswerRepository;
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
@RequestMapping("/v1/correct_answer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CorrectAnswerController extends ABasicController{
  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

  @Autowired
  CorrectAnswerMapper correctAnswerMapper;

  @GetMapping(value = "/student_submission", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CRA_ED_CPL')")
  public ApiMessageDto<ResponseListDto<List<CorrectAnswerDisplayDto>>> listStudentCorrectAnswer(CorrectAnswerCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CorrectAnswerDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CorrectAnswerDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<CorrectAnswer> correctAnswers = correctAnswerRepository.findAll(criteria.getSpecification(), pageable);
    List<CorrectAnswerDisplayDto> correctAnswerDisplayDtos = correctAnswerMapper.fromEntityToCorrectAnswerDisplayDtoList(correctAnswers.getContent());
    responseListDto.setContent(correctAnswerDisplayDtos);
    responseListDto.setTotalElements(correctAnswers.getTotalElements());
    responseListDto.setTotalPages(correctAnswers.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list correct answer success");
    return apiMessageDto;
  }
}
