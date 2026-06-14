package com.base.auth.repository;

import com.base.auth.model.ReviewSubmission;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewSubmissionRepository extends JpaRepository<ReviewSubmission, Long>,
    JpaSpecificationExecutor<ReviewSubmission> {
  @Transactional
  void deleteAllByStudentId(Long studentId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE rs " +
          "FROM db_it_dream_review_submission rs " +
          "JOIN db_it_dream_student_submission ss ON rs.student_submission_id = ss.id " +
          "WHERE ss.task_question_id = :taskQuestionId",
      nativeQuery = true)
  void deleteAllByTaskQuestionId(@Param("taskQuestionId") Long taskQuestionId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE rs " +
          "FROM db_it_dream_review_submission rs " +
          "JOIN db_it_dream_student_submission ss ON rs.student_submission_id = ss.id " +
          "JOIN db_it_dream_task_question tq ON ss.task_question_id = tq.id " +
          "WHERE tq.task_id = :taskId",
      nativeQuery = true)
  void deleteAllByTaskId(@Param("taskId") Long taskId);

  @Query("SELECT CASE WHEN COUNT(rs) > 0 THEN TRUE ELSE FALSE END " +
      "FROM ReviewSubmission rs " +
      "JOIN rs.studentSubmission ss " +
      "WHERE ss.studentTaskProgress.id = :studentTaskProgressId")
  boolean existsByStudentTaskProgressId(
      @Param("studentTaskProgressId") Long studentTaskProgressId);
}
