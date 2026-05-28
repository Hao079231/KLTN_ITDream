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

  public Integer generateOrderInParent(Long simulationId, Long parentId, Integer kind, Boolean isShowSimulation){
    Integer maxOrder = taskRepository.findMaxOrderInParent(simulationId, parentId, kind, isShowSimulation);
    return maxOrder == null ? 1 : maxOrder + 1;
  }

  public void updateTaskPosition(Task task, UpdateTaskPositionForm form){
    Boolean oldShow = task.getIsShowSimulation();
    Boolean newShow = form.getIsShowSimulation();

    Integer oldOrder = task.getOrderInParent();

    Long simulationId = task.getSimulation().getId();

    taskRepository.decreaseOrderAfterRemove(simulationId, null,ITDreamConstant.TASK_KIND_TASK,oldShow,oldOrder);

    if(!oldShow.equals(newShow)){
      Integer maxOrder = generateOrderInParent(simulationId,null,ITDreamConstant.TASK_KIND_TASK,newShow);

      task.setOrderInParent(maxOrder);
      task.setIsShowSimulation(newShow);

      taskRepository.save(task);

      return;
    }

    taskRepository.increaseOrderForInsert(simulationId,null, ITDreamConstant.TASK_KIND_TASK, newShow,form.getNewOrder());

    task.setOrderInParent(form.getNewOrder());

    taskRepository.save(task);
  }

  public void updateSubtaskPosition(Task task, UpdateTaskPositionForm form){

    if(form.getIsShowSimulation()){
      throw new BadRequestException(
          "Subtask cannot move to show simulation list", ErrorCode.TASK_ERROR_POSITION);
    }

    if(form.getNewParentId() == null){
      throw new BadRequestException("Subtask must have parent",ErrorCode.TASK_ERROR_MUST_HAVE_PARENT);
    }

    Task newParent = taskRepository.findById(form.getNewParentId())
        .orElseThrow(() -> new NotFoundException("Parent not found",ErrorCode.TASK_ERROR_NOT_FOUND));

    if(newParent.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      throw new BadRequestException("Parent must be task", ErrorCode.TASK_ERROR_PARENT_KIND_TASK);
    }

    if(newParent.getIsShowSimulation()){
      throw new BadRequestException("Cannot move subtask into show simulation task", ErrorCode.TASK_ERROR_POSITION);
    }

    Long simulationId = task.getSimulation().getId();

    Long oldParentId = task.getParent().getId();

    Integer oldOrder = task.getOrderInParent();

    taskRepository.decreaseOrderAfterRemove(simulationId,oldParentId,ITDreamConstant.TASK_KIND_SUBTASK, ITDreamConstant.TASK_HIDE_WITH_SIMULATION, oldOrder);

    if(!oldParentId.equals(newParent.getId())){

      Integer maxOrder = generateOrderInParent(simulationId,newParent.getId(),ITDreamConstant.TASK_KIND_SUBTASK,ITDreamConstant.TASK_HIDE_WITH_SIMULATION);

      task.setParent(newParent);
      task.setOrderInParent(maxOrder);

      taskRepository.save(task);

      return;
    }

    taskRepository.increaseOrderForInsert(simulationId,newParent.getId(),ITDreamConstant.TASK_KIND_SUBTASK,ITDreamConstant.TASK_HIDE_WITH_SIMULATION, form.getNewOrder());

    task.setOrderInParent(form.getNewOrder());

    taskRepository.save(task);
  }

  private void updateOrderAfterDelete(Task task){
    if(task.getKind().equals(ITDreamConstant.TASK_KIND_TASK)){

      taskRepository.decreaseOrderAfterDelete(
          task.getSimulation().getId(),
          null,
          ITDreamConstant.TASK_KIND_TASK,
          task.getIsShowSimulation(),
          task.getOrderInParent()
      );

      return;
    }

    if(task.getKind().equals(ITDreamConstant.TASK_KIND_SUBTASK)){
      taskRepository.decreaseOrderAfterDelete(
          task.getSimulation().getId(),
          task.getParent().getId(),
          ITDreamConstant.TASK_KIND_SUBTASK,
          ITDreamConstant.TASK_HIDE_WITH_SIMULATION,
          task.getOrderInParent()
      );
    }
  }
}
