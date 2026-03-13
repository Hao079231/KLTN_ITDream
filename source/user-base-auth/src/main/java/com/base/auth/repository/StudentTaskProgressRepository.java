package com.base.auth.repository;

import com.base.auth.model.StudentTaskProgress;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

  List<StudentTaskProgress> findAllByTaskId(Long taskId);

  @Transactional
  void deleteAllBySimulationEnrollmentStudentId(Long studentId);

  Boolean existsBySimulationEnrollmentIdAndStatus(Long simulationEnrollmentId, Integer studentTaskProgress);
}
