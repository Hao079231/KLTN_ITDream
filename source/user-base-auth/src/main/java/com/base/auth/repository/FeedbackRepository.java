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

  Boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

  @Transactional
  void deleteAllByCourseId(Long courseId);
//  void deleteByCourseId(Long simulationId);
//
//  int countBySimulationId(Long simulationId);
//
//  List<Feedback> findAllByStudentId(Long studentId);
//
//  boolean existsByStudentIdAndSimulationId(long studentId, Long simulationId);
//
//  @Modifying
//  @Transactional
//  @Query(
//      value = "DELETE r " +
//          "FROM db_it_dream_review r " +
//          "JOIN db_it_dream_simulation s ON r.simulation_id = s.id " +
//          "WHERE s.educator_id = :educatorId",
//      nativeQuery = true
//  )
//  void deleteAllByEducatorId(@Param("educatorId") Long educatorId);
}
