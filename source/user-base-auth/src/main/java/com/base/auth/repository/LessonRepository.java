package com.base.auth.repository;

import com.base.auth.model.Lesson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
  Boolean existsByChapterIdAndTitle(Long chapterId, String title);

  Optional<Lesson> findFirstByChapterIdAndPreviousIsNull(Long chapterId);

  Boolean existsByChapterId(Long chapterId);

  @Query("SELECT COUNT(l.id) FROM Lesson l JOIN l.chapter c WHERE c.course.id = :courseId")
  Integer countLessonInCourse(@Param("courseId") Long courseId);
}
