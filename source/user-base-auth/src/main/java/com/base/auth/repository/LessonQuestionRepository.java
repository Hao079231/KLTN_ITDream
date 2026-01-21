package com.base.auth.repository;

import com.base.auth.model.LessonQuestion;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonQuestionRepository extends JpaRepository<LessonQuestion, Long>,
    JpaSpecificationExecutor<LessonQuestion> {

  Boolean existsByQuestionAndLessonId(String question, Long lessonId);

  Boolean existsByQuestionAndOptionsAndLessonId(String question, String options, Long lessonId);

  @Modifying
  @Transactional
  @Query("delete from LessonQuestion lq where lq.lesson.id = :lessonId")
  void deleteAllByLessonId(@Param("lessonId") Long lessonId);

//  @Modifying
//  @Transactional
//  @Query(value =
//      "DELETE FROM db_it_dream_task_question tq " +
//          "WHERE " +
//          "   tq.task_id = :taskId " +
//          "   OR (" +
//          "       (SELECT kind FROM db_it_dream_task WHERE id = :taskId) = 1 " +
//          "       AND tq.task_id IN (SELECT id FROM db_it_dream_task WHERE parent_id = :taskId)" +
//          "   )",
//      nativeQuery = true)
//  void deleteAllByTaskAndSubtask(@Param("taskId") Long taskId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE tq FROM db_it_dream_task_question tq " +
//          "JOIN db_it_dream_task t ON tq.task_id = t.id " +
//          "WHERE t.simulation_id = :simulationId", nativeQuery = true)
//  void deleteAllByCourseId(@Param("simulationId") Long simulationId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE tq FROM db_it_dream_task_question tq " +
//      "JOIN db_it_dream_task t ON tq.task_id = t.id " +
//      "JOIN db_it_dream_simulation s ON t.simulation_id = s.id " +
//      "WHERE s.educator_id = :educatorId", nativeQuery = true)
//  void deleteAllByEducatorId(@Param("educatorId") Long educatorId);
//
//  Optional<LessonQuestion> findByQuestionAndTaskId(String question, Long taskId);
//
//  LessonQuestion findFirstByTaskId(Long taskId);
//
//
//  boolean existsByOptions(String options);
//
//  boolean existsByQuestionAndOptionsAndTaskId(String question, String options, Long taskId);
//
//  boolean existsByQuestionAndTaskId(String question, Long taskId);
//
//  boolean existsByTaskId(Long taskId);
}
