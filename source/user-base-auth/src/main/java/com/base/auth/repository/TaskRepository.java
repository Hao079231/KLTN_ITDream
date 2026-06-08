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
  Integer countTaskBySimulationId(@Param("simulationId") Long simulationId);

  List<Task> findAllByParentId(Long parentId);

  @Query("SELECT MAX(t.orderInParent) " +
      "FROM Task t " +
      "WHERE t.simulation.id = :simulationId " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.kind = :kind")
  Integer findMaxOrderInParent(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind
  );

  @Transactional
  @Modifying
  @Query("UPDATE Task t " +
      "SET t.orderInParent = t.orderInParent + 1 " +
      "WHERE t.simulation.id = :simulationId " +
      "AND t.kind = :kind " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.orderInParent >= :newOrder " +
      "AND t.orderInParent < :oldOrder")
  void increaseOrderWhenMoveUp(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("newOrder") Integer newOrder,
      @Param("oldOrder") Integer oldOrder
  );

  @Transactional
  @Modifying
  @Query("UPDATE Task t " +
      "SET t.orderInParent = t.orderInParent - 1 " +
      "WHERE t.simulation.id = :simulationId " +
      "AND t.kind = :kind " +
      "AND ((:parentId IS NULL AND t.parent IS NULL) " +
      "OR (t.parent.id = :parentId)) " +
      "AND t.orderInParent > :oldOrder " +
      "AND t.orderInParent <= :newOrder")
  void decreaseOrderWhenMoveDown(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("oldOrder") Integer oldOrder,
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
      "AND t.orderInParent > :deletedOrder")
  void decreaseOrderAfterDelete(
      @Param("simulationId") Long simulationId,
      @Param("parentId") Long parentId,
      @Param("kind") Integer kind,
      @Param("deletedOrder") Integer deletedOrder
  );

  @Query("SELECT COUNT(t) FROM Task t WHERE t.parent.id = :parentId")
  Integer countByParentId(@Param("parentId") Long parentId);
}
