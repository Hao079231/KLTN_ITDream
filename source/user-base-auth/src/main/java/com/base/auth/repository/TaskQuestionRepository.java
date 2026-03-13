package com.base.auth.repository;

import com.base.auth.model.TaskQuestion;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskQuestionRepository extends JpaRepository<TaskQuestion, Long>,
    JpaSpecificationExecutor<TaskQuestion> {

  Boolean existsByQuestionAndTaskId(String question, Long taskId);

  Boolean existsByQuestionAndOptionsAndTaskId(String question, String options, Long taskId);

  @Modifying
  @Transactional
  @Query("delete from TaskQuestion tq where tq.task.id = :taskId")
  void deleteAllByTaskId(@Param("taskId") Long taskId);

  Integer countByTaskId(Long taskId);
}
