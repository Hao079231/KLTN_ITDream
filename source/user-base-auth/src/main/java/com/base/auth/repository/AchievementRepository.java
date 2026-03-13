package com.base.auth.repository;

import com.base.auth.model.Achievement;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AchievementRepository extends JpaRepository<Achievement, Long>,
    JpaSpecificationExecutor<Achievement> {

  @Transactional
  void deleteAllByStudentId(Long studentId);

  @Modifying
  @Query("UPDATE Achievement a set a.simulation = null where a.simulation.id = :simulationId")
  @Transactional
  void setNullBySimulationId(@Param("simulationId") Long simulationId);

  List<Achievement> findAllByStudentId(Long studentId);
}
