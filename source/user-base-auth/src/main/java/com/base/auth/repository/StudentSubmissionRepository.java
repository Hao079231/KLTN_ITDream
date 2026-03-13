package com.base.auth.repository;

import com.base.auth.model.StudentSubmission;
import java.util.List;
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
  @Query("DELETE FROM StudentSubmission ssm WHERE ssm.studentTaskProgress.id = :studentTaskProgressId")
  @Transactional
  void deleteAllByStudentTaskProgressId(@Param("studentTaskProgressId") Long studentTaskProgressId);

  @Query("SELECT COUNT(ssm.id) "
      + "FROM StudentSubmission ssm WHERE ssm.studentTaskProgress.id = :studentTaskProgressId")
  Integer countByStudentTaskProgressId(@Param("studentTaskProgressId") Long studentTaskProgressId);

  @Query("SELECT COUNT(DISTINCT ssm.taskQuestion.id)"
      + " FROM StudentSubmission ssm WHERE ssm.studentTaskProgress.id = :studentTaskProgressId")
  Integer countDistinctQuestionByStudentTaskProgress(Long studentTaskProgressId);

  @Modifying
  @Query("DELETE FROM StudentSubmission ssm WHERE ssm.taskQuestion.id = :taskQuestionId")
  @Transactional
  void deleteAllByTaskQuestionId(@Param("taskQuestionId") Long taskQuestionId);

  List<StudentSubmission> findAllByTaskQuestionId(Long taskQuestionId);

  List<StudentSubmission> findAllByTaskQuestionTaskId(Long taskId);

  @Transactional
  void deleteAllByTaskQuestionTaskId(Long taskId);

  @Transactional
  void deleteAllByStudentTaskProgressSimulationEnrollmentStudentId(Long studentId);

  Boolean existsByStudentTaskProgressIdAndAnswer(Long studentTaskProgressId, String answer);

  Optional<StudentSubmission> findByTaskQuestionId(Long taskQuestionId);

  @Modifying
  @Query(" delete from StudentSubmission ssm where ssm.taskQuestion.id in "
      + "(select tq.id from TaskQuestion tq where tq.task.id = :taskId)")
  @Transactional
  void deleteAllByStudentTaskProgressTaskId(Long taskId);

  @Query("select count(ssm.id) " +
      "from StudentSubmission ssm " +
      "where ssm.studentTaskProgress.simulationEnrollment.id = :enrollmentId")
  long countBySimulationEnrollmentId(@Param("enrollmentId") Long enrollmentId);

  @Query("SELECT COUNT(ssm) " +
      "FROM StudentSubmission ssm " +
      "JOIN ssm.studentTaskProgress stp " +
      "JOIN stp.simulationEnrollment se " +
      "WHERE se.simulation.id = :simulationId " +
      "AND se.student.id = :studentId")
  Long countStudentSubmissionBySimulationAndStudent(@Param("simulationId") Long simulationId, @Param("studentId") Long studentId);

}
