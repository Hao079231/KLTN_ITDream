package com.base.auth.controller;

import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.questionQuizHistory.CreateQuestionQuizHistoryForm;
import com.base.auth.mapper.QuestionQuizHistoryMapper;
import com.base.auth.model.StudentSubmission;
import com.base.auth.model.Task;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.TaskQuestion;
import com.base.auth.model.QuestionQuizHistory;
import com.base.auth.model.Student;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/question_quiz_history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class QuestionQuizHistoryController extends ABasicController{
  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  TaskQuestionRepository taskQuestionRepository;

  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  QuestionQuizHistoryMapper questionQuizHistoryMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('QQH_ST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateQuestionQuizHistoryForm form,  BindingResult bindingResult){
    if (!isStudent()){
      throw new UnauthorizationException("User is not a student");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Student student = studentRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Student not found", ErrorCode.USER_ERROR_NOT_FOUND));
    StudentTaskProgress studentTaskProgress = studentTaskProgressRepository.findById(form.getStudentTaskProgressId())
        .orElseThrow(() -> new NotFoundException("Task progress not found", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND));
    Task task = studentTaskProgress.getTask();
    if (task.getTotalError().equals(studentTaskProgress.getErrorCount())){
      throw new BadRequestException("Please reset the task to try again", ErrorCode.STUDENT_TASK_PROGRESS_ERROR_FAIL);
    }

    TaskQuestion taskQuestion = null;
    if (form.getTaskQuestionId() != null){
      taskQuestion = taskQuestionRepository.findById(form.getTaskQuestionId())
          .orElseThrow(() -> new NotFoundException("Task question not found", ErrorCode.TASK_QUESTION_ERROR_NOT_FOUND));
      if (!studentTaskProgress.getTask().getId().equals(taskQuestion.getTask().getId())){
        throw new BadRequestException("The question is not in the task", ErrorCode.STUDENT_SUBMISSION_ERROR_NOT_CREATE);
      }
    }

    Boolean existSubmission = studentSubmissionRepository.existsByStudentTaskProgressIdAndAnswer(studentTaskProgress.getId(), form.getAnswer());
    if (existSubmission){
      throw new BadRequestException("The question has been answered", ErrorCode.STUDENT_SUBMISSION_ERROR_NOT_CREATE);
    }

    if (Boolean.TRUE.equals(form.getIsCorrect())){ // Nếu đã làm bằng text, file hoặc trả lời trắc nghiệm đúng
      StudentSubmission studentSubmission = new StudentSubmission();
      studentSubmission.setAnswer(form.getAnswer());
      studentSubmission.setStudentTaskProgress(studentTaskProgress);
      studentSubmission.setTaskQuestion(taskQuestion);
      studentSubmissionRepository.save(studentSubmission);
    } else if (taskQuestion != null){ // Nếu làm trắc nghiệm sai
        QuestionQuizHistory questionQuizHistory = questionQuizHistoryMapper.fromCreateQuestionQuizHistoryFormToEntity(form);
        questionQuizHistory.setStudentTaskProgress(studentTaskProgress);
        questionQuizHistory.setTaskQuestion(taskQuestion);
        questionQuizHistoryRepository.save(questionQuizHistory);
        studentTaskProgress.setErrorCount(studentTaskProgress.getErrorCount() + 1);
        studentTaskProgressRepository.save(studentTaskProgress);
    }

    apiMessageDto.setMessage("Create question quiz history success");
    return apiMessageDto;
  }
}
