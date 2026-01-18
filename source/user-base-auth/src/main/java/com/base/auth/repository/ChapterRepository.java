package com.base.auth.repository;

import com.base.auth.model.Chapter;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterRepository extends JpaRepository<Chapter, Long>, JpaSpecificationExecutor<Chapter> {

  Boolean existsByName(String name);

  @Query("SELECT MAX(c.chapterOrder) FROM Chapter c WHERE c.course.id = :courseId")
  Integer findMaxChapterOrderByCourseId(@Param("courseId") Long courseId);

  Boolean existsByNameAndCourseId(String name, Long courseId);

  List<Chapter> findAllByCourseId(Long courseId);
}
