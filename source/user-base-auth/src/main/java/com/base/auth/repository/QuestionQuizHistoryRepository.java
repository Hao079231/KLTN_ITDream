package com.base.auth.repository;

import com.base.auth.model.QuestionQuizHistory;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionQuizHistoryRepository extends JpaRepository<QuestionQuizHistory, Long> {
  @Modifying
  @Query("DELETE FROM QuestionQuizHistory qqh WHERE qqh.taskQuestion.id = :taskQuestionId")
  @Transactional
  void deleteAllByTaskQuestionId(@Param("taskQuestionId") Long taskQuestionId);

  @Modifying
  @Query("DELETE FROM QuestionQuizHistory qqh WHERE qqh.studentTaskProgress.id = :studentTaskProgressId")
  @Transactional
  void deleteAllByStudentTaskProgressId(@Param("studentTaskProgressId") Long studentTaskProgressId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE q FROM db_it_dream_question_quiz_history q " +
          "JOIN db_it_dream_task_question tq ON q.task_question_id = tq.id " +
          "WHERE tq.task_id = :taskId",
      nativeQuery = true)
  void deleteAllByTaskId(@Param("taskId") Long taskId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE q FROM db_it_dream_question_quiz_history q " +
          "JOIN db_it_dream_student_task_progress stp ON q.student_task_progress_id = stp.id " +
          "JOIN db_it_dream_simulation_enrollment se ON stp.simulation_enrollment_id = se.id " +
          "WHERE se.student_id = :studentId",
      nativeQuery = true)
  void deleteAllByStudentId(@Param("studentId") Long studentId);

  @Modifying
  @Query("delete from QuestionQuizHistory qh where qh.taskQuestion.id in "
      + "(select tq.id from TaskQuestion tq where tq.task.id = :taskId)")
  @Transactional
  void deleteAllByStudentTaskProgressTaskId(Long taskId);
}
