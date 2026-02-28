package com.base.auth.repository;

import com.base.auth.model.Achievement;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AchievementRepository extends JpaRepository<Achievement, Long>,
    JpaSpecificationExecutor<Achievement> {

  @Transactional
  void deleteAllByStudentId(Long studentId);

  @Modifying
  @Query("UPDATE Achievement a set a.course = null where a.course.id = :courseId")
  @Transactional
  void setNullByCourseId(@Param("courseId") Long courseId);

  List<Achievement> findAllByStudentId(Long studentId);
}
