package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.Comment;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.Task;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class CommentCriteria {
  @NotNull(message = "taskId is required")
  private Long taskId;
  private Long userId;

  public Specification<Comment> getSpecification() {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      Join<Comment, Task> taskJoin = root.join("task", JoinType.INNER);
      Join<Comment, Comment> rootJoin = root.join("root", JoinType.LEFT);
      predicates.add(cb.equal(taskJoin.get("id"), taskId));

      // 2. Filters
      if (getTaskId() != null) {
        predicates.add(cb.equal(taskJoin.get("id"), getTaskId()));
      }

      if (getUserId() != null) {
        Join<Comment, Account> userJoin = root.join("user", JoinType.LEFT);
        predicates.add(cb.equal(userJoin.get("id"), getUserId()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}