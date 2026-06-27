package com.base.auth.model.criteria;

import com.base.auth.model.Comment;
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

  public Specification<Comment> getSpecification() {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      Join<Comment, Task> taskJoin = root.join("task", JoinType.INNER);
      Join<Comment, Comment> rootJoin = root.join("root", JoinType.LEFT);
      predicates.add(cb.equal(taskJoin.get("id"), taskId));

      if (query.getResultType() != Long.class && query.getResultType() != long.class) {
        Expression<Object> threadDate = cb.selectCase()
            .when(cb.isNull(root.get("root")), root.get("createdDate"))
            .otherwise(rootJoin.get("createdDate"));

        query.orderBy(cb.desc(threadDate), cb.asc(root.get("createdDate")));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}