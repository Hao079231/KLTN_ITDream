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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class CommentCriteria {
  @NotNull(message = "taskId is required")
  private Long taskId;
  private Long userId;
  private String keyWord;
  private Boolean onlyRoot;
  private Long simulationEnrollmentId;

  public Specification<Comment> getSpecification() {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // 1. Joins - Cần join với root để lấy ngày tạo của comment gốc
      Join<Comment, Task> taskJoin = root.join("task", JoinType.INNER);
      Join<Comment, Comment> rootJoin = root.join("root", JoinType.LEFT);

      // 2. Filters
      if (getTaskId() != null) {
        predicates.add(cb.equal(taskJoin.get("id"), getTaskId()));
      }
      if (getUserId() != null) {
        Join<Comment, Account> userJoin = root.join("user", JoinType.LEFT);
        predicates.add(cb.equal(userJoin.get("id"), getUserId()));
      }
      if (StringUtils.isNotBlank(getKeyWord())) {
        predicates.add(cb.like(cb.lower(root.get("content")), "%" + getKeyWord().toLowerCase() + "%"));
      }
      if (Boolean.TRUE.equals(getOnlyRoot())) {
        predicates.add(cb.isNull(root.get("parent")));
      }
      if (getSimulationEnrollmentId() != null) {
        Join<Comment, SimulationEnrollment> enrollmentJoin = root.join("simulationEnrollment", JoinType.LEFT);
        predicates.add(cb.equal(enrollmentJoin.get("id"), getSimulationEnrollmentId()));
      }

      // 3. Logic sắp xếp san phẳng (Flattened Grouping)
      // Kiểm tra nếu không phải query count (paging) thì mới order
      if (query.getResultType() != Long.class && query.getResultType() != long.class) {

        // Xác định ngày để nhóm: Nếu là con thì lấy ngày của root, nếu là cha thì lấy ngày của chính nó
        Expression<Object> threadDate = cb.selectCase()
            .when(cb.isNull(root.get("root")), root.get("createdDate"))
            .otherwise(rootJoin.get("createdDate"));

        query.orderBy(
            cb.desc(threadDate),        // 1. Thread nào có bài gốc mới nhất thì đứng đầu
            cb.asc(root.get("createdDate")) // 2. Trong thread đó, ai comment trước đứng trước (Cha -> Con -> Cháu)
        );
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}