package com.base.auth.model.criteria;

import com.base.auth.model.Course;
import com.base.auth.model.CourseEnrollment;
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
public class CourseEnrollmentCriteria {
  private Long studentId;
  private Long courseId;
  private Integer status;

  public Specification<CourseEnrollment> getSpecification() {
    return new Specification<CourseEnrollment>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<CourseEnrollment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<CourseEnrollment, Student> studentJoin = root.join("student", JoinType.INNER);
        Join<CourseEnrollment, Course> courseJoin = root.join("course", JoinType.INNER);
        if (getStudentId() != null){
          predicates.add(cb.equal(studentJoin.get("id"), getStudentId()));
        }

        if (getCourseId() != null){
          predicates.add(cb.equal(courseJoin.get("id"), getCourseId()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
