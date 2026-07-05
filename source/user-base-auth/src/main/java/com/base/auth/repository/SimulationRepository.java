package com.base.auth.repository;

import com.base.auth.model.Simulation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SimulationRepository extends JpaRepository<Simulation, Long>,
    JpaSpecificationExecutor<Simulation> {
  void deleteAllByEducatorId(Long educatorId);

  List<Simulation> findAllByEducatorId(Long educatorId);

  Boolean existsByCategoryId(Long id);

  boolean existsByTitleAndEducatorId(String title, long educatorId);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM db_it_dream_simulation_job WHERE simulation_id = :simulationId",
      nativeQuery = true)
  void deleteSimulationJobBySimulationId(@Param("simulationId") Long simulationId);
}
