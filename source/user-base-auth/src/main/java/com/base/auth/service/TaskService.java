package com.base.auth.service;

import com.base.auth.model.Task;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskService {
  @Autowired
  TaskRepository taskRepository;

  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  TaskQuestionRepository taskQuestionRepository;

  @Autowired
  CommentRepository commentRepository;

  public void deleteAllByTask(Task task){
    reviewSubmissionRepository.deleteAllByTaskId(task.getId());
    studentSubmissionRepository.deleteAllByTaskQuestionTaskId(task.getId());
    questionQuizHistoryRepository.deleteAllByTaskQuestionTaskId(task.getId());
    studentTaskProgressRepository.deleteAllByTaskId(task.getId());
    taskQuestionRepository.deleteAllByTaskId(task.getId());
    commentRepository.deleteAllByTaskId(task.getId());
    taskRepository.delete(task);
  }
}
