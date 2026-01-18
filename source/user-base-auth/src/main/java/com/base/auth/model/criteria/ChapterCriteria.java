package com.base.auth.model.criteria;

import com.base.auth.model.Chapter;
import com.base.auth.model.Course;
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
public class ChapterCriteria {
  @NotNull(message = "courseId is required")
  private Long courseId;
  private Integer status;

  public Specification<Chapter> getSpecification() {
    return new Specification<Chapter>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Chapter> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Chapter, Course> courseJoin = root.join("course", JoinType.INNER);
        predicates.add(cb.equal(courseJoin.get("id"), getCourseId()));

        if (getStatus() != null){
          predicates.add(cb.equal(courseJoin.get("status"), getStatus()));
        }
        query.orderBy(cb.asc(root.get("chapterOrder")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
