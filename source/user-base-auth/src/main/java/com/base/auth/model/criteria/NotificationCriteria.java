package com.base.auth.model.criteria;

import com.base.auth.model.Notification;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class NotificationCriteria {
  public Specification<Notification> getSpecification() {
    return new Specification<Notification>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
