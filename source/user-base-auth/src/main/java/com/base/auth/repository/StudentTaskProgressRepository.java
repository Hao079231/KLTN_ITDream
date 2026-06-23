package com.base.auth.repository;

import com.base.auth.model.StudentTaskProgress;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentTaskProgressRepository extends JpaRepository<StudentTaskProgress, Long>,
    JpaSpecificationExecutor<StudentTaskProgress> {
  @Transactional
  void deleteAllByTaskId(Long taskId);

  @Query("SELECT COUNT(stp.id) FROM StudentTaskProgress stp JOIN stp.task t "
      + "JOIN t.simulation s WHERE stp.simulationEnrollment.id = :enrollmentId "
      + "AND stp.status = :status AND s.id = :simulationId")
  Integer countCompletedTaskInSimulation(
      @Param("enrollmentId") Long enrollmentId,
      @Param("simulationId") Long simulationId,
      @Param("status") Integer status);

  Optional<StudentTaskProgress> findByTaskIdAndSimulationEnrollmentStudentId(Long taskId, long studentId);

  @Transactional
  @Modifying
  @Query(value =
      "DELETE stp FROM db_it_dream_student_task_progress stp " +
          "JOIN db_it_dream_simulation_enrollment se ON stp.simulation_enrollment_id = se.id " +
          "WHERE se.student_id = :studentId",
      nativeQuery = true)
  void deleteAllByStudentId(@Param("studentId") Long studentId);

  @Query("SELECT COUNT(stp.id) " +
      "FROM StudentTaskProgress stp " +
      "JOIN stp.task t " +
      "WHERE t.parent.id = :taskId " +
      "AND stp.status = :status " +
      "AND stp.simulationEnrollment.id = :enrollmentId")
  Integer countCompletedSubtaskInTask(@Param("taskId") Long taskId, @Param("status") Integer status, @Param("enrollmentId") Long enrollmentId);

  Boolean existsBySimulationEnrollmentIdAndTask_KindAndStatus(Long simulationEnrollmentId, Integer taskKindSubtask, Integer studentTaskProgressInProgress);

  @Query(
      "SELECT CASE WHEN COUNT(stp) > 0 THEN TRUE ELSE FALSE END " +
          "FROM StudentTaskProgress stp " +
          "WHERE stp.simulationEnrollment.id = :enrollmentId " +
          "AND stp.task.kind = :taskKind " +
          "AND EXISTS (" +
          "   SELECT rs.id " +
          "   FROM ReviewSubmission rs " +
          "   WHERE rs.studentSubmission.studentTaskProgress.id = stp.id" +
          ")"
  )
  boolean existsAnyReviewedTask(@Param("enrollmentId") Long enrollmentId, @Param("taskKind") Integer taskKind);
}
