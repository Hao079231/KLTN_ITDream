package com.base.auth.repository;

import com.base.auth.model.Lesson;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
  Boolean existsByChapterIdAndTitle(Long chapterId, String title);

  Optional<Lesson> findFirstByChapterIdAndPreviousIsNull(Long chapterId);

  List<Lesson> findAllByChapterId(Long chapterId);

  Boolean existsByChapterId(Long chapterId);
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE FROM db_it_dream_lesson WHERE parent_id IS NOT NULL AND course_id = :courseId", nativeQuery = true)
//  void deleteAllSubLessonByCourseId(@Param("courseId") Long courseId);
//
//  @Modifying
//  @Transactional
//  @Query(value = "DELETE FROM db_it_dream_lesson WHERE parent_id IS NULL AND course_id = :courseId", nativeQuery = true)
//  void deleteAllLessonByCourseId(@Param("courseId") Long courseId);
//
//  @Modifying
//  @Transactional
//  @Query(value =
//      "DELETE FROM db_it_dream_lesson " +
//          "WHERE parent_id IS NOT NULL AND course_id IN (SELECT id FROM db_it_dream_course WHERE educator_id = :educatorId)",
//      nativeQuery = true)
//  void deleteAllSubLessonByEducatorId(@Param("educatorId") Long educatorId);
//
//  @Modifying
//  @Transactional
//  @Query(value =
//      "DELETE FROM db_it_dream_Lesson " +
//          "WHERE parent_id IS NULL AND course_id IN (SELECT id FROM db_it_dream_course WHERE educator_id = :educatorId)",
//      nativeQuery = true)
//  void deleteAllLessonByEducatorId(@Param("educatorId") Long educatorId);
//
//  void deleteAllByParentId(Long id);
//
//
//  Optional<Lesson> findByIdAndKind(Long parentId, Integer LessonKindLesson);
//
//  Boolean existsByNameAndKindAndcourseId(String name, Integer LessonKindLesson, Long courseId);
//
//
//  List<Lesson> findAllByParentId(Long id);
//
//  List<Lesson> findAllByCourseId(Long course);
//
//  @Query("SELECT t FROM Lesson t " +
//      "JOIN t.course s " +
//      "JOIN s.educator e " +
//      "WHERE e.id = :educatorId")
//  List<Lesson> findAllByEducatorId(@Param("educatorId") Long educatorId);
//
//  Long countBycourseId(Long courseId);
//
//  Boolean existsByTitleAndKindAndParentIdAndcourseId(String title, Integer LessonKindSubLesson, Long parentId, Long courseId);
//
//  Long countByKindAndCourseId(Integer LessonKindLesson, Long id);
//
//  Long countByKindAndParentId(Integer kindSubLesson, Long id);
//
//  Lesson findByKindAndId(Integer LessonKindLesson, Long parentId);
}
