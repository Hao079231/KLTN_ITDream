package com.base.auth.model.criteria;

import com.base.auth.model.StudentSubmission;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.ReviewSubmission;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class ReviewSubmissionCriteria {
  private Long studentTaskProgressId;

  public Specification<ReviewSubmission> getSpecification() {
    return new Specification<ReviewSubmission>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<ReviewSubmission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<ReviewSubmission, StudentSubmission> studentSubmissionJoin =
            root.join("studentSubmission", JoinType.INNER);

        Join<StudentSubmission, StudentTaskProgress> studentTaskProgressJoin =
            studentSubmissionJoin.join("studentTaskProgress", JoinType.INNER);

        if (studentTaskProgressId != null) {
          predicates.add(cb.equal(studentTaskProgressJoin.get("id"), studentTaskProgressId));
        }

        query.distinct(true);
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
