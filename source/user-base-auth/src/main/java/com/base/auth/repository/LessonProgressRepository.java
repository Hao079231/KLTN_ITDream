package com.base.auth.repository;

import com.base.auth.model.LessonProgress;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long>,
    JpaSpecificationExecutor<LessonProgress> {

  void deleteAllByCourseEnrollmentCourseId(Long courseId);

  @Transactional
  void deleteAllByLessonId(Long lessonId);

  @Query("SELECT COUNT(lp.id) FROM LessonProgress lp JOIN lp.lesson l "
      + "JOIN l.chapter c WHERE lp.courseEnrollment.id = :enrollmentId "
      + "AND lp.status = :status AND c.course.id = :courseId")
  Integer countCompletedLessonInCourse(
      @Param("enrollmentId") Long enrollmentId,
      @Param("courseId") Long courseId,
      @Param("status") Integer status);

  Optional<LessonProgress> findByLessonIdAndCourseEnrollmentStudentId(Long lessonId, long studentId);

  List<LessonProgress> findAllByLessonId(Long lessonId);

  @Transactional
  void deleteAllByCourseEnrollmentStudentId(Long studentId);

  Boolean existsByCourseEnrollmentIdAndStatus(Long courseEnrollmentId, Integer lessonProgressInProgress);
//
//  void deleteAllByStudentId(Long studentId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE sstp FROM db_it_dream_student_subtask_progress sstp " +
//      "WHERE sstp.task_id = :taskId " +
//      "OR sstp.task_id IN (SELECT id FROM db_it_dream_task WHERE parent_id = :taskId)", nativeQuery = true)
//  void deleteAllByTaskAndSubtask(@Param("taskId") Long taskId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE sstp FROM db_it_dream_student_subtask_progress sstp " +
//          "JOIN db_it_dream_task t ON sstp.task_id = t.id " +
//          "WHERE t.simulation_id = :simulationId", nativeQuery = true)
//  void deleteAllByCourseId(@Param("simulationId") Long simulationId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE sstp FROM db_it_dream_student_subtask_progress sstp " +
//          "JOIN db_it_dream_task t ON sstp.task_id = t.id " +
//          "JOIN db_it_dream_simulation sim ON t.simulation_id = sim.id " +
//          "WHERE sim.educator_id = :educatorId", nativeQuery = true)
//  void deleteAllByEducatorId(@Param("educatorId") Long educatorId);
//
//  boolean existsByStudentIdAndTaskSimulationId(long studentId, Long simulationId);
//
//  Long countByStateAndStudentIdAndTaskCourseId(Integer state, long studentId, Long simulationId);
//
//  @Query("SELECT COUNT(sstp) " +
//      "FROM LessonProgress sstp " +
//      "JOIN sstp.task t " +
//      "WHERE sstp.student.id = :studentId " +
//      "AND t.simulation.id = :simulationId " +
//      "AND sstp.state = :state " +
//      "AND t.kind = :kind")
//  Long countByStateAndStudentIdAndTaskSimulationIdAndTaskKind(
//      @Param("state") Integer state,
//      @Param("studentId") Long studentId,
//      @Param("simulationId") Long simulationId,
//      @Param("kind") Integer kind);


//  Long countByStateAndStudentIdAndTaskKindAndTaskParentId(Integer progress, long currentUser, Integer taskKindSubtask, Long taskId);
}
