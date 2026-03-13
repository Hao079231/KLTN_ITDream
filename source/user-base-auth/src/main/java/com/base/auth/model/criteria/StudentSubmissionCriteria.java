package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.Simulation;
import com.base.auth.model.StudentSubmission;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.StudentTaskProgress;
import com.base.auth.model.Student;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class StudentSubmissionCriteria {
  private Long simulationId;
  private String studentUsername;

  public Specification<StudentSubmission> getSpecification() {
    return new Specification<StudentSubmission>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<StudentSubmission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // Chỉ fetch khi không phải count query
        if (query.getResultType() != Long.class) {
          // fetch TaskQuestion
          root.fetch("taskQuestion", JoinType.LEFT);

          // fetch studentTaskProgress → lesson
          Fetch<StudentSubmission, StudentTaskProgress> studentTaskProgressFetch = root.fetch("studentTaskProgress", JoinType.LEFT);
          studentTaskProgressFetch.fetch("task", JoinType.LEFT);
        }

        List<Predicate> predicates = new ArrayList<>();
        Join<StudentSubmission, StudentTaskProgress> studentTaskProgressJoin = root.join("studentTaskProgress", JoinType.INNER);
        Join<StudentTaskProgress, SimulationEnrollment> enrollmentJoin = studentTaskProgressJoin.join("simulationEnrollment", JoinType.INNER);
        Join<SimulationEnrollment, Simulation> simulationJoin = enrollmentJoin.join("simulation", JoinType.INNER);
        Join<SimulationEnrollment, Student> studentJoin = enrollmentJoin.join("student", JoinType.INNER);
        Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);

        if (getSimulationId() != null) {
          predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));
        }

        if (StringUtils.isNotEmpty(getStudentUsername())) {
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getStudentUsername().toLowerCase().trim() + "%"));
        }
        query.distinct(true);

        return cb.and(predicates.toArray(new Predicate[0]));
      }
    };
  }
}
