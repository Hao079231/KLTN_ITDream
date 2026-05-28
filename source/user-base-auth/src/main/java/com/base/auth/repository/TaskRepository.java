package com.base.auth.repository;

import com.base.auth.model.Task;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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

  @Query("SELECT MAX(t.orderInParent) " +
      "FROM Task t " +
      "WHERE t.simulation.id = :simulationId " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.kind = :kind " +
      "AND t.isShowSimulation = :isShowSimulation")
  Integer findMaxOrderInParent(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("isShowSimulation") Boolean isShowSimulation
  );

  @Query("SELECT t FROM Task t " +
      "WHERE t.simulation.id = :simulationId " +
      "AND t.kind = :kind " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.isShowSimulation = :isShowSimulation " +
      "ORDER BY t.orderInParent ASC")
  List<Task> findAllForReOrder(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("isShowSimulation") Boolean isShowSimulation
  );

  @Transactional
  @Modifying
  @Query("UPDATE Task t " +
      "SET t.orderInParent = t.orderInParent - 1 " +
      "WHERE t.simulation.id = :simulationId " +
      "AND t.kind = :kind " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.isShowSimulation = :isShowSimulation " +
      "AND t.orderInParent > :currentOrder")
  void decreaseOrderAfterRemove(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("isShowSimulation") Boolean isShowSimulation,
      @Param("currentOrder") Integer currentOrder
  );

  @Transactional
  @Modifying
  @Query("UPDATE Task t " +
      "SET t.orderInParent = t.orderInParent + 1 " +
      "WHERE t.simulation.id = :simulationId " +
      "AND t.kind = :kind " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.isShowSimulation = :isShowSimulation " +
      "AND t.orderInParent >= :newOrder")
  void increaseOrderForInsert(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("isShowSimulation") Boolean isShowSimulation,
      @Param("newOrder") Integer newOrder
  );

  @Transactional
  @Modifying
  @Query("UPDATE Task t " +
      "SET t.orderInParent = t.orderInParent - 1 " +
      "WHERE t.simulation.id = :simulationId " +
      "AND t.kind = :kind " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.isShowSimulation = :isShowSimulation " +
      "AND t.orderInParent > :deletedOrder")
  void decreaseOrderAfterDelete(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("isShowSimulation") Boolean isShowSimulation,
      @Param("deletedOrder") Integer deletedOrder
  );
}
