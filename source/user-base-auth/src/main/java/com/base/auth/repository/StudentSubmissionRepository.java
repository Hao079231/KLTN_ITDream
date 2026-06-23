package com.base.auth.repository;

import com.base.auth.model.StudentSubmission;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long>,
    JpaSpecificationExecutor<StudentSubmission> {

  @Modifying
  @Transactional
  @Query("DELETE FROM StudentSubmission ssm WHERE ssm.studentTaskProgress.id = :studentTaskProgressId AND ssm.taskQuestion IS NOT NULL")
  void deleteQuestionSubmissionsByProgressId(@Param("studentTaskProgressId") Long studentTaskProgressId);

  @Query("SELECT COUNT(ssm.id) "
      + "FROM StudentSubmission ssm WHERE ssm.studentTaskProgress.id = :studentTaskProgressId")
  Integer countByStudentTaskProgressId(@Param("studentTaskProgressId") Long studentTaskProgressId);

  @Modifying
  @Query("DELETE FROM StudentSubmission ssm WHERE ssm.taskQuestion.id = :taskQuestionId")
  @Transactional
  void deleteAllByTaskQuestionId(@Param("taskQuestionId") Long taskQuestionId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE ss FROM db_it_dream_student_submission ss " +
          "JOIN db_it_dream_task_question tq ON ss.task_question_id = tq.id " +
          "WHERE tq.task_id = :taskId",
      nativeQuery = true)
  void deleteAllByTaskId(@Param("taskId") Long taskId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE ss FROM db_it_dream_student_submission ss " +
          "JOIN db_it_dream_student_task_progress stp ON ss.student_task_progress_id = stp.id " +
          "JOIN db_it_dream_simulation_enrollment se ON stp.simulation_enrollment_id = se.id " +
          "WHERE se.student_id = :studentId",
      nativeQuery = true)
  void deleteAllByStudentId(@Param("studentId") Long studentId);

  Boolean existsByStudentTaskProgressIdAndAnswer(Long studentTaskProgressId, String answer);

  @Query("SELECT ssm FROM StudentSubmission ssm WHERE ssm.studentTaskProgress.id = :studentTaskProgressId AND " +
      "((:taskQuestionId IS NULL AND ssm.taskQuestion IS NULL) OR (ssm.taskQuestion.id = :taskQuestionId))")
  Optional<StudentSubmission> findByStudentTaskProgressIdAndTaskQuestionId(
      @Param("studentTaskProgressId") Long studentTaskProgressId,
      @Param("taskQuestionId") Long taskQuestionId);
}
