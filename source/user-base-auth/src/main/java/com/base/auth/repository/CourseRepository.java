package com.base.auth.repository;

import com.base.auth.model.Course;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Long>,
    JpaSpecificationExecutor<Course> {
  Page<Course> findAllByStatus(Integer statusActive, Pageable pageable);

  void deleteAllByEducatorId(Long educatorId);

  List<Course> findAllByEducatorId(Long educatorId);

  Boolean existsByCategoryId(Long id);

  boolean existsByTitleAndEducatorId(String title, long educatorId);
}
