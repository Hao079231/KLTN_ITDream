package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.questionQuizHistory.CreateQuestionQuizHistoryForm;
import com.base.auth.mapper.QuestionQuizHistoryMapper;
import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonProgress;
import com.base.auth.model.LessonQuestion;
import com.base.auth.model.QuestionQuizHistory;
import com.base.auth.model.Student;
import com.base.auth.repository.CorrectAnswerRepository;
import com.base.auth.repository.LessonProgressRepository;
import com.base.auth.repository.LessonQuestionRepository;
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
  LessonProgressRepository lessonProgressRepository;

  @Autowired
  LessonQuestionRepository lessonQuestionRepository;

  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

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
    LessonProgress lessonProgress = lessonProgressRepository.findById(form.getLessonProgressId())
        .orElseThrow(() -> new NotFoundException("Lesson progress not found", ErrorCode.LESSON_PROGRESS_ERROR_NOT_FOUND));
    Lesson lesson = lessonProgress.getLesson();
    if (lesson.getTotalError().equals(lessonProgress.getErrorCount())){
      throw new BadRequestException("Please reset the lesson to try again", ErrorCode.LESSON_PROGRESS_ERROR_FAIL);
    }
    LessonQuestion lessonQuestion = lessonQuestionRepository.findById(form.getLessonQuestionId())
        .orElseThrow(() -> new NotFoundException("Lesson question not found", ErrorCode.LESSON_QUESTION_ERROR_NOT_FOUND));

    if (!lessonProgress.getLesson().getId().equals(lessonQuestion.getLesson().getId())){
      throw new BadRequestException("The question is not in the lesson", ErrorCode.CORRECT_ANSWER_ERROR_NOT_CREATE);
    }

    Boolean existCorrectAnswer = correctAnswerRepository.existsByLessonProgressIdAndAnswer(lessonProgress.getId(), form.getAnswer());
    if (existCorrectAnswer){
      throw new BadRequestException("The question has been answered", ErrorCode.CORRECT_ANSWER_ERROR_NOT_CREATE);
    }

    if (Boolean.TRUE.equals(form.getIsCorrect())){
      CorrectAnswer correctAnswer = new CorrectAnswer();
      correctAnswer.setAnswer(form.getAnswer());
      correctAnswer.setLessonProgress(lessonProgress);
      correctAnswer.setLessonQuestion(lessonQuestion);
      correctAnswerRepository.save(correctAnswer);
      questionQuizHistoryRepository.deleteAllByLessonQuestionId(lessonQuestion.getId());
      student.setScore(student.getScore() + ITDreamConstant.SCORE_COMPLETE_QUESTION);
      studentRepository.save(student);
    } else if (ITDreamConstant.QUESTION_TYPE_QUIZ.equals(lessonQuestion.getQuestionType())){
        QuestionQuizHistory questionQuizHistory = questionQuizHistoryMapper.fromCreateQuestionQuizHistoryFormToEntity(form);
        questionQuizHistory.setLessonProgress(lessonProgress);
        questionQuizHistory.setLessonQuestion(lessonQuestion);
        questionQuizHistoryRepository.save(questionQuizHistory);

        lessonProgress.setErrorCount(lessonProgress.getErrorCount() + 1);
        lessonProgressRepository.save(lessonProgress);
    }

    apiMessageDto.setMessage("Create question quiz history success");
    return apiMessageDto;
  }
}
