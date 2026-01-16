package com.base.auth.model.criteria;

import com.base.auth.model.Course;
import com.base.auth.model.Educator;
import com.base.auth.model.Lesson;
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
public class LessonCriteria {
  @NotNull(message = "courseId is required")
  private Long courseId;
  private Long educatorId;
  private Integer status;
  private Long parentId;

  public Specification<Lesson> getSpecification() {
    return new Specification<Lesson>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Lesson> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        Join<Lesson, Course> joinCourse = root.join("course", JoinType.INNER);
        predicates.add(cb.equal(joinCourse.get("id"), getCourseId()));

        if (getParentId() != null){
          Join<Lesson, Lesson> taskJoin = root.join("parent", JoinType.INNER);
          predicates.add(cb.equal(taskJoin.get("id"), getParentId()));
        }

        if (getEducatorId() != null) {
          Join<Course, Educator> joinEducator = joinCourse.join("educator", JoinType.INNER);
          predicates.add(cb.equal(joinEducator.get("id"), getEducatorId()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(joinCourse.get("status"), getStatus()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
