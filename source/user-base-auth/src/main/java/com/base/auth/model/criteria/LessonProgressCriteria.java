package com.base.auth.model.criteria;

import com.base.auth.model.CourseEnrollment;
import com.base.auth.model.LessonProgress;
import com.base.auth.model.Student;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class LessonProgressCriteria {
  @NotNull(message = "courseEnrollmentId is required")
  private Long courseEnrollmentId;

  public Specification<LessonProgress> getSpecification() {
    return new Specification<LessonProgress>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<LessonProgress> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        root.fetch("lesson", JoinType.LEFT);
        query.distinct(true);
        Join<LessonProgress, CourseEnrollment> courseEnrollmentJoin = root.join("courseEnrollment", JoinType.INNER);
        predicates.add(cb.equal(courseEnrollmentJoin.get("id"), getCourseEnrollmentId()));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
