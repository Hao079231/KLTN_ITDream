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
  void deleteAllByTaskQuestionTaskId(Long taskId);

  @Transactional
  void deleteAllByStudentTaskProgressSimulationEnrollmentStudentId(Long studentId);

  @Modifying
  @Query("delete from QuestionQuizHistory qh where qh.taskQuestion.id in "
      + "(select tq.id from TaskQuestion tq where tq.task.id = :taskId)")
  @Transactional
  void deleteAllByStudentTaskProgressTaskId(Long taskId);
}
