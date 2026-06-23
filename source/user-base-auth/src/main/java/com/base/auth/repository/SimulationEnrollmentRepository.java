package com.base.auth.repository;

import com.base.auth.model.SimulationEnrollment;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SimulationEnrollmentRepository extends JpaRepository<SimulationEnrollment, Long>,
    JpaSpecificationExecutor<SimulationEnrollment> {

  void deleteAllBySimulationId(Long simulationId);

  Optional<SimulationEnrollment> findBySimulationId(Long simulationId);

  @Transactional
  void deleteAllByStudentId(Long studentId);

  Optional<SimulationEnrollment> findByStudentIdAndSimulationId(Long studentId, Long simulationId);

  Boolean existsBySimulationIdAndStudentId(Long simulationId, long currentUser);

  // Tìm enrollment theo simulationId và ID của Account của student (dùng cho completeReview)
  Optional<SimulationEnrollment> findBySimulationIdAndStudentAccountId(Long simulationId, Long accountId);
}
