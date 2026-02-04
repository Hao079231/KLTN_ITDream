package com.base.auth.repository;

import com.base.auth.model.CorrectAnswer;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CorrectAnswerRepository extends JpaRepository<CorrectAnswer, Long>,
    JpaSpecificationExecutor<CorrectAnswer> {

  @Modifying
  @Query("DELETE FROM CorrectAnswer ca WHERE ca.lessonProgress.id = :lessonProgressId")
  @Transactional
  void deleteAllByLessonProgressId(@Param("lessonProgressId") Long lessonProgressId);

  @Query("SELECT COUNT(ca.id) "
      + "FROM CorrectAnswer ca WHERE ca.lessonProgress.id = :lessonProgressId")
  Integer countByLessonProgressId(@Param("lessonProgressId") Long lessonProgressId);

  @Query("SELECT COUNT(DISTINCT ca.lessonQuestion.id)"
      + " FROM CorrectAnswer ca WHERE ca.lessonProgress.id = :lessonProgressId")
  Integer countDistinctQuestionByLessonProgress(Long lessonProgressId);

  @Modifying
  @Query("DELETE FROM CorrectAnswer ca WHERE ca.lessonQuestion.id = :lessonQuestionId")
  @Transactional
  void deleteAllByLessonQuestionId(@Param("lessonQuestionId") Long lessonQuestionId);

  List<CorrectAnswer> findAllByLessonQuestionId(Long lessonQuestionId);

  List<CorrectAnswer> findAllByLessonQuestionLessonId(Long lessonId);

  @Transactional
  void deleteAllByLessonQuestionLessonId(Long lessonId);

  void deleteAllByLessonProgressCourseEnrollmentStudentId(Long studentId);

  Boolean existsByLessonProgressIdAndAnswer(Long lessonProgressId, String answer);

  Optional<CorrectAnswer> findByLessonQuestionId(Long LessonQuestionId);

  @Modifying
  @Query(" delete from CorrectAnswer ca where ca.lessonQuestion.id in "
      + "(select lq.id from LessonQuestion lq where lq.lesson.id = :lessonId)")
  @Transactional
  void deleteAllByLessonProgressLessonId(Long lessonId);
//  void deleteAllByStudentSubTaskProgressId(Long studentSubTaskProgressId);
//
//  Optional<CorrectAnswer> findFirstByStudentSubTaskProgressId(Long studentSubTaskProgressId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE stq FROM db_it_dream_student_task_question_progress stq " +
//      "JOIN db_it_dream_student_subtask_progress ss ON stq.student_subtask_progress_id = ss.id " +
//      "WHERE ss.student_id = :studentId", nativeQuery = true)
//  void deleteAllByStudentId(Long studentId);
//
//  @Modifying
//  @Transactional
//  @Query("DELETE FROM CorrectAnswer stq WHERE stq.taskQuestion.id = :taskQuestionId")
//  void deleteAllByTaskQuestionId(Long taskQuestionId);
//
//  @Query("SELECT COUNT(stq) FROM CorrectAnswer stq " +
//      "WHERE stq.studentSubTaskProgress.id = :studentSubTaskProgressId AND stq.isCorrect = true")
//  int countCorrectByStudentSubTaskProgressId(@Param("studentSubTaskProgressId") Long studentSubTaskProgressId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE stqp FROM db_it_dream_student_task_question_progress stqp " +
//          "WHERE stqp.task_question_id IN ( " +
//          "   SELECT tq.id FROM db_it_dream_task_question tq " +
//          "   WHERE tq.task_id = :taskId " +
//          "   OR tq.task_id IN (SELECT id FROM db_it_dream_task WHERE parent_id = :taskId))", nativeQuery = true)
//  void deleteAllByTaskAndSubtask(@Param("taskId") Long taskId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE stqp FROM db_it_dream_student_task_question_progress stqp " +
//          "JOIN db_it_dream_task_question tq ON stqp.task_question_id = tq.id " +
//          "JOIN db_it_dream_task t ON tq.task_id = t.id " +
//          "WHERE t.simulation_id = :simulationId", nativeQuery = true)
//  void deleteAllByCourseId(@Param("simulationId") Long simulationId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE stqp FROM db_it_dream_student_task_question_progress stqp " +
//          "JOIN db_it_dream_task_question tq ON stqp.task_question_id = tq.id " +
//          "JOIN db_it_dream_task t ON tq.task_id = t.id " +
//          "JOIN db_it_dream_simulation sim ON t.simulation_id = sim.id " +
//          "WHERE sim.educator_id = :educatorId", nativeQuery = true)
//  void deleteAllByEducatorId(@Param("educatorId") Long educatorId);
//
//  @Query("SELECT stq FROM CorrectAnswer stq " +
//      "JOIN stq.studentSubTaskProgress sstp " +
//      "JOIN stq.taskQuestion tq " +
//      "JOIN tq.task t " +
//      "WHERE sstp.student.id = :studentId AND t.simulation.id = :simulationId " +
//      "ORDER BY t.id, tq.id")
//  Page<CorrectAnswer> findAllByStudentIdAndSimulationId(
//      @Param("studentId") Long studentId,
//      @Param("simulationId") Long simulationId,
//      Pageable pageable);
//
//  @Query("SELECT stq FROM CorrectAnswer stq " +
//      "JOIN stq.studentSubTaskProgress sstp " +
//      "WHERE sstp.student.id = :studentId")
//  List<CorrectAnswer> findAllByStudentId( @Param("studentId") Long studentId);
//
//  boolean existsByTaskQuestionIdAndStudentSubTaskProgressIdAndIsCorrect(Long taskQuestionId, Long studentSubtaskProgressId, boolean isCorrect);
}
