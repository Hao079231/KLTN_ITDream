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

  @Query("select count(rs.id) " +
      "from ReviewSubmission rs " +
      "where rs.studentSubmission.studentTaskProgress.simulationEnrollment.id = :enrollmentId")
  long countBySimulationEnrollmentId(@Param("enrollmentId") Long enrollmentId);

  @Transactional
  void deleteAllByStudentId(Long studentId);

  @Query("SELECT COUNT(rs) " +
      "FROM ReviewSubmission rs " +
      "JOIN rs.studentSubmission ssm " +
      "JOIN ssm.studentTaskProgress stp " +
      "JOIN stp.task t " +
      "JOIN t.simulation s " +
      "WHERE s.id = :simulationId " +
      "AND rs.student.id = :studentId")
  Long countReviewBySimulationAndStudent(@Param("simulationId") Long simulationId, @Param("studentId") Long studentId);

//  @Transactional
//  @Modifying
//  @Query("DELETE FROM ReviewSubmission rs " +
//      "WHERE rs.studentSubmission.taskQuestion.id = :taskQuestionId")
//  void deleteAllByTaskQuestionId(@Param("taskQuestionId") Long taskQuestionId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE rs " +
          "FROM db_it_dream_review_submission rs " +
          "JOIN db_it_dream_student_submission ss ON rs.student_submission_id = ss.id " +
          "WHERE ss.task_question_id = :taskQuestionId",
      nativeQuery = true)
  void deleteAllByTaskQuestionId(@Param("taskQuestionId") Long taskQuestionId);

//  @Transactional
//  @Modifying
//  @Query("DELETE FROM ReviewSubmission rs " +
//      "WHERE rs.studentSubmission.taskQuestion.task.id = :taskId")
//  void deleteAllByTaskId(@Param("taskId") Long taskId);

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

  ReviewSubmission findByStudentSubmissionIdAndStudentId(Long studentSubmissionId, Long studentId);
}
