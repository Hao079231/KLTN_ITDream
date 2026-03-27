package com.base.auth.repository;

import com.base.auth.model.Task;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
  Boolean existsBySimulationIdAndName(Long simulationId, String name);

  Boolean existsBySimulationIdAndNameAndTitle(Long simulationId, String name, String title);

  List<Task> findAllBySimulationId(Long simulationId);

  @Query("SELECT COUNT(t) FROM Task t WHERE t.simulation.id = :simulationId")
  @Transactional
  Integer countTaskBySimulationId(@Param("simulationId") Long simulationId);

  List<Task> findAllByParentId(Long parentId);
}
