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
}
