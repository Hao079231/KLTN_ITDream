package com.base.auth.repository;

import com.base.auth.model.ReviewSubmission;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewSubmissionRepository extends JpaRepository<ReviewSubmission, Long>,
    JpaSpecificationExecutor<ReviewSubmission> {

  @Query("select count(rs.id) " +
      "from ReviewSubmission rs " +
      "where rs.correctAnswer.lessonProgress.courseEnrollment.id = :enrollmentId")
  long countByCourseEnrollmentId(@Param("enrollmentId") Long enrollmentId);

  @Transactional
  void deleteAllByStudentId(Long studentId);

  @Query("SELECT COUNT(rs) " +
      "FROM ReviewSubmission rs " +
      "JOIN rs.correctAnswer ca " +
      "JOIN ca.lessonProgress lp " +
      "JOIN lp.lesson l " +
      "JOIN l.chapter ch " +
      "JOIN ch.course c " +
      "WHERE c.id = :courseId " +
      "AND rs.student.id = :studentId")
  Long countReviewByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

  @Transactional
  @Modifying
  @Query("DELETE FROM ReviewSubmission rs " +
      "WHERE rs.correctAnswer.lessonQuestion.id = :lessonQuestionId")
  void deleteAllByLessonQuestionId(@Param("lessonQuestionId") Long lessonQuestionId);

  @Transactional
  @Modifying
  @Query("DELETE FROM ReviewSubmission rs " +
      "WHERE rs.correctAnswer.lessonQuestion.lesson.id = :lessonId")
  void deleteAllByLessonId(@Param("lessonId") Long lessonId);

  @Transactional
  @Modifying
  @Query("DELETE FROM ReviewSubmission rs " +
      "WHERE rs.correctAnswer.lessonQuestion.lesson.chapter.id = :chapterId")
  void deleteAllByChapterId(@Param("chapterId") Long chapterId);

  ReviewSubmission findByCorrectAnswerIdAndStudentId(Long correctAnswerId, Long studentId);
}
