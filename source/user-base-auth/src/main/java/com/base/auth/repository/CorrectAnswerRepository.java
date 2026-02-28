package com.base.auth.repository;

import com.base.auth.model.CorrectAnswer;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
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

  @Transactional
  void deleteAllByLessonProgressCourseEnrollmentStudentId(Long studentId);

  Boolean existsByLessonProgressIdAndAnswer(Long lessonProgressId, String answer);

  Optional<CorrectAnswer> findByLessonQuestionId(Long LessonQuestionId);

  @Modifying
  @Query(" delete from CorrectAnswer ca where ca.lessonQuestion.id in "
      + "(select lq.id from LessonQuestion lq where lq.lesson.id = :lessonId)")
  @Transactional
  void deleteAllByLessonProgressLessonId(Long lessonId);

  @Query("select count(ca.id) " +
      "from CorrectAnswer ca " +
      "where ca.lessonProgress.courseEnrollment.id = :enrollmentId")
  long countByCourseEnrollmentId(@Param("enrollmentId") Long enrollmentId);

  @Query("SELECT COUNT(ca) " +
      "FROM CorrectAnswer ca " +
      "JOIN ca.lessonProgress lp " +
      "JOIN lp.courseEnrollment ce " +
      "WHERE ce.course.id = :courseId " +
      "AND ce.student.id = :studentId")
  Long countCorrectAnswerByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
