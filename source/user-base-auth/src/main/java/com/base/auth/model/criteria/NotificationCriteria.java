package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.Notification;
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
public class NotificationCriteria {
  @NotNull(message = "Cần truyền Id người nhận")
  private Long receiverId;

  public Specification<Notification> getSpecification() {
    return new Specification<Notification>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if(getReceiverId() != null){
          Join<Notification, Account> accountJoin = root.join("receiver", JoinType.INNER);
          predicates.add(cb.equal(accountJoin.get("id"), getReceiverId()));
        }
        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
