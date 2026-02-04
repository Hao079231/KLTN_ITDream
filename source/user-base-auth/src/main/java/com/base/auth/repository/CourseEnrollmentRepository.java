package com.base.auth.repository;

import com.base.auth.model.CourseEnrollment;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long>,
    JpaSpecificationExecutor<CourseEnrollment> {

  void deleteAllByCourseId(Long courseId);

  Optional<CourseEnrollment> findByCourseId(Long courseId);

  void deleteAllByStudentId(Long studentId);
}
