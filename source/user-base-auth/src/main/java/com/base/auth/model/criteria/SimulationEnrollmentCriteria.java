package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.Simulation;
import com.base.auth.model.SimulationEnrollment;
import com.base.auth.model.Student;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class SimulationEnrollmentCriteria {
  private Long studentId;
  private Long simulationId;
  private Integer status;
  private String username;
  private Date startDate;
  private Date endDate;

  public Specification<SimulationEnrollment> getSpecification() {
    return new Specification<SimulationEnrollment>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<SimulationEnrollment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<SimulationEnrollment, Student> studentJoin = root.join("student", JoinType.INNER);
        Join<SimulationEnrollment, Simulation> simulationJoin = root.join("simulation", JoinType.INNER);
        if (StringUtils.isNotBlank(getUsername())){
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getUsername().toLowerCase() + "%"));
        }

        if (getStudentId() != null){
          predicates.add(cb.equal(studentJoin.get("id"), getStudentId()));
        }

        if (getSimulationId() != null){
          predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        if (getStartDate() != null && getEndDate() != null) {
          predicates.add(cb.between(root.get("modifiedDate"), getStartDate(), getEndDate()));
        } else if (getStartDate() != null) {
          predicates.add(cb.greaterThanOrEqualTo(root.get("modifiedDate"), getStartDate()));
        } else if (getEndDate() != null) {
          predicates.add(cb.lessThanOrEqualTo(root.get("modifiedDate"), getEndDate()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
