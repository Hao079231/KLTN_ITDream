package com.base.auth.model.criteria;

import com.base.auth.model.Simulation;
import com.base.auth.model.Feedback;
import java.util.ArrayList;
import java.util.Date;
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
public class FeedbackCriteria {
  private Long simulationId;
  private Date startDate;
  private Date endDate;

  public Specification<Feedback> getSpecification() {
    return new Specification<Feedback>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Feedback> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<Feedback, Simulation> simulationJoin = root.join("simulation", JoinType.INNER);
        predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));

        if (getStartDate() != null && getEndDate() != null) {
          predicates.add(cb.between(root.get("modifiedDate"), getStartDate(), getEndDate()));
        } else if (getStartDate() != null) {
          predicates.add(cb.greaterThanOrEqualTo(root.get("modifiedDate"), getStartDate()));
        } else if (getEndDate() != null) {
          predicates.add(cb.lessThanOrEqualTo(root.get("modifiedDate"), getEndDate()));
        }

        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
