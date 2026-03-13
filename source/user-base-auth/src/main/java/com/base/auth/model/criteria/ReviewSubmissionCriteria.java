package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.StudentSubmission;
import com.base.auth.model.Simulation;
import com.base.auth.model.Task;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.ReviewSubmission;
import com.base.auth.model.Student;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class ReviewSubmissionCriteria {
  private Long simulationId;
  private String studentUsername;
  private Long studentId;

  public Specification<ReviewSubmission> getSpecification() {
    return new Specification<ReviewSubmission>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<ReviewSubmission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<ReviewSubmission, StudentSubmission> studentSubmissionJoin = root.join("studentSubmission", JoinType.INNER);
        Join<StudentSubmission, StudentTaskProgress> studentTaskProgressJoin = studentSubmissionJoin.join("studentTaskProgress", JoinType.INNER);
        Join<StudentTaskProgress, Task> taskJoin = studentTaskProgressJoin.join("task", JoinType.INNER);
        Join<Task, Simulation> simulationJoin = taskJoin.join("simulation", JoinType.INNER);

        if (getSimulationId() != null) {
          predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));
        }

        if (StringUtils.isNotEmpty(getStudentUsername())) {
          Join<ReviewSubmission, Student> studentJoin = root.join("student", JoinType.INNER);
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getStudentUsername().toLowerCase().trim() + "%"));
        }

        if (getStudentId() != null){
          Join<ReviewSubmission, Student> studentJoin = root.join("student", JoinType.INNER);
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.equal(accountJoin.get("id"), getStudentId()));
        }

        query.distinct(true);
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
