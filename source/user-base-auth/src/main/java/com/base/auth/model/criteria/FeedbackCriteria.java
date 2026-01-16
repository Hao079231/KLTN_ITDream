package com.base.auth.model.criteria;

import com.base.auth.model.Course;
import com.base.auth.model.Feedback;
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
public class FeedbackCriteria {
  @NotNull(message = "simulationId required")
  private Long simulationId;

  public Specification<Feedback> getSpecification() {
    return new Specification<Feedback>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Feedback> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Feedback, Course> simulationJoin = root.join("simulation", JoinType.INNER);
        predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
