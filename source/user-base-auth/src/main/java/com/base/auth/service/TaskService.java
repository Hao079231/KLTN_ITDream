package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.model.Task;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import com.base.auth.repository.TaskQuestionRepository;
import com.base.auth.repository.TaskRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired
  UserBaseApiService userBaseApiService;

  public void deleteAllTask(Task task){
    List<Task> subtasks = taskRepository.findAllByParentId(task.getId());
    for(Task subtask: subtasks){
      deleteAllTask(subtask);
    }

    reviewSubmissionRepository.deleteAllByTaskId(task.getId());
    studentSubmissionRepository.deleteAllByTaskId(task.getId());
    questionQuizHistoryRepository.deleteAllByTaskId(task.getId());
    studentTaskProgressRepository.deleteAllByTaskId(task.getId());
    taskQuestionRepository.deleteAllByTaskId(task.getId());
    commentRepository.deleteAllByTaskId(task.getId());
    taskRepository.delete(task);
  }

  public void  deleteFileInTask(Task task){
    if (StringUtils.isNotBlank(task.getImagePath()) &&
        !task.getImagePath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(task.getImagePath());
    }

    if (StringUtils.isNotBlank(task.getFilePath()) &&
        !task.getFilePath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(task.getFilePath());
    }

    if (StringUtils.isNotBlank(task.getVideoPath()) &&
        !task.getVideoPath().toLowerCase().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(task.getVideoPath());
    }
  }
}
