package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ErrorCode;
import com.base.auth.exception.BadRequestException;
import com.base.auth.exception.NotFoundException;
import com.base.auth.form.task.UpdateTaskPositionForm;
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
    updateOrderAfterDelete(task);
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

  public Integer generateOrderInParent(Long simulationId, Long parentId, Integer kind){
    Integer maxOrder = taskRepository.findMaxOrderInParent(simulationId, parentId, kind);
    return maxOrder == null ? 1 : maxOrder + 1;
  }

  @Transactional
  public void updateTaskPosition(Task task, UpdateTaskPositionForm form){

    if(form.getNewOrder() == null || form.getNewOrder() < 1){
      throw new BadRequestException("New order invalid",ErrorCode.TASK_ERROR_POSITION);
    }

    if(task.getOrderInParent().equals(form.getNewOrder())){
      return;
    }

    if(form.getNewOrder() < task.getOrderInParent()){
      taskRepository.increaseOrderWhenMoveUp(task.getSimulation().getId(),null,ITDreamConstant.TASK_KIND_TASK,form.getNewOrder(),task.getOrderInParent());
    } else {
      taskRepository.decreaseOrderWhenMoveDown(task.getSimulation().getId(),null,ITDreamConstant.TASK_KIND_TASK, task.getOrderInParent(), form.getNewOrder());
    }
    task.setOrderInParent(form.getNewOrder());
    taskRepository.save(task);
  }

  @Transactional
  public void updateSubtaskPosition(Task task, UpdateTaskPositionForm form){
    if(form.getNewParentId() == null){
      throw new BadRequestException("Subtask must have parent",ErrorCode.TASK_ERROR_MUST_HAVE_PARENT);
    }

    Task newParent = taskRepository.findById(form.getNewParentId())
        .orElseThrow(() -> new NotFoundException("Parent not found",ErrorCode.TASK_ERROR_NOT_FOUND));

    if(newParent.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      throw new BadRequestException("Parent must be task",ErrorCode.TASK_ERROR_PARENT_KIND_TASK);
    }

    if(!newParent.getSimulation().getId().equals(task.getSimulation().getId())){
      throw new BadRequestException("Cannot move subtask to another simulation",ErrorCode.TASK_ERROR_POSITION);
    }

    if(!task.getParent().getId().equals(form.getNewParentId())){
      taskRepository.decreaseOrderAfterDelete(task.getSimulation().getId(),task.getParent().getId(),ITDreamConstant.TASK_KIND_SUBTASK,task.getOrderInParent());
      Integer maxOrder = generateOrderInParent(task.getSimulation().getId(),form.getNewParentId(), ITDreamConstant.TASK_KIND_SUBTASK);
      task.setParent(newParent);
      task.setOrderInParent(maxOrder);
      taskRepository.save(task);
      return;
    }

    if(form.getNewOrder() == null || form.getNewOrder() < 1){
      throw new BadRequestException("New order invalid", ErrorCode.TASK_ERROR_POSITION);
    }

    if(task.getOrderInParent().equals(form.getNewOrder())){
      return;
    }

    if(form.getNewOrder() < task.getOrderInParent()){
      taskRepository.increaseOrderWhenMoveUp(task.getSimulation().getId(), task.getParent().getId(), ITDreamConstant.TASK_KIND_SUBTASK,form.getNewOrder(),task.getOrderInParent());
    } else {
      taskRepository.decreaseOrderWhenMoveDown(task.getSimulation().getId(), task.getParent().getId(), ITDreamConstant.TASK_KIND_SUBTASK,task.getOrderInParent(),form.getNewOrder());
    }
    task.setOrderInParent(form.getNewOrder());
    taskRepository.save(task);
  }

  private void updateOrderAfterDelete(Task task){
    if(task.getKind().equals(ITDreamConstant.TASK_KIND_TASK)){
      taskRepository.decreaseOrderAfterDelete(task.getSimulation().getId(),null,ITDreamConstant.TASK_KIND_TASK,task.getOrderInParent());
      return;
    }

    if(task.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      taskRepository.decreaseOrderAfterDelete(task.getSimulation().getId(),task.getParent().getId(),ITDreamConstant.TASK_KIND_SUBTASK,task.getOrderInParent());
    }
  }
}
