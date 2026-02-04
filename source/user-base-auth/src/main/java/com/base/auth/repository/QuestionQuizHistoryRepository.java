package com.base.auth.repository;

import com.base.auth.model.QuestionQuizHistory;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionQuizHistoryRepository extends JpaRepository<QuestionQuizHistory, Long> {
  @Modifying
  @Query("DELETE FROM QuestionQuizHistory qqh WHERE qqh.lessonQuestion.id = :lessonQuestionId")
  @Transactional
  void deleteAllByLessonQuestionId(@Param("lessonQuestionId") Long lessonQuestionId);

  @Modifying
  @Query("DELETE FROM QuestionQuizHistory qqh WHERE qqh.lessonProgress.id = :lessonProgressId")
  @Transactional
  void deleteAllByLessonProgressId(@Param("lessonProgressId") Long lessonProgressId);

  @Transactional
  void deleteAllByLessonQuestionLessonId(Long lessonId);

  void deleteAllByLessonProgressCourseEnrollmentStudentId(Long studentId);

  @Modifying
  @Query("delete from QuestionQuizHistory qh where qh.lessonQuestion.id in "
      + "(select lq.id from LessonQuestion lq where lq.lesson.id = :lessonId)")
  @Transactional
  void deleteAllByLessonProgressLessonId(Long lessonId);
}
