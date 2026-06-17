package com.base.auth.repository;

import com.base.auth.model.Feedback;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {
  Boolean existsByStudentIdAndSimulationId(Long studentId, Long simulationId);

  @Transactional
  void deleteAllBySimulationId(Long simulationId);

  @Transactional
  void deleteAllByStudentId(Long id);

  List<Feedback> findAllByStudentId(Long studentId);
}
