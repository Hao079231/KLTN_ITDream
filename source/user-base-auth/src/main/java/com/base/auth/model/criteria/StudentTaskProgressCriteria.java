package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.Student;
import com.base.auth.model.StudentTaskProgress;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class StudentTaskProgressCriteria {
  @NotNull(message = "simulationEnrollmentId is required")
  private Long simulationEnrollmentId;
  private String studentUsername;
  private Integer status;

  public Specification<StudentTaskProgress> getSpecification() {
    return new Specification<StudentTaskProgress>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<StudentTaskProgress> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        root.fetch("task", JoinType.LEFT);
        query.distinct(true);
        Join<StudentTaskProgress, SimulationEnrollment> simulationEnrollmentJoin = root.join("simulationEnrollment", JoinType.INNER);
        predicates.add(cb.equal(simulationEnrollmentJoin.get("id"), getSimulationEnrollmentId()));

        if (StringUtils.isNotBlank(getStudentUsername())){
          Join<SimulationEnrollment, Student> studentJoin = simulationEnrollmentJoin.join("student", JoinType.INNER);
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getStudentUsername().toLowerCase().trim() + "%"));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
