package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.Achievement;
import com.base.auth.model.Simulation;
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
public class AchievementCriteria {
  private Long studentId;
  private String fullName;
  private String title;

  public Specification<Achievement> getSpecification() {
    return new Specification<Achievement>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Achievement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (getStudentId() != null){
          Join<Achievement, Student> studentJoin = root.join("student", JoinType.INNER);
          predicates.add(cb.equal(studentJoin.get("id"), getStudentId()));
        }

        if (StringUtils.isNotEmpty(getFullName())){
          Join<Achievement, Student> studentJoin = root.join("student", JoinType.INNER);
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.like(cb.lower(accountJoin.get("fullName")), "%" + getFullName().toLowerCase() + "%"));
        }

        if (StringUtils.isNotEmpty(getTitle())){
          Join<Achievement, Simulation> simulationJoin = root.join("simulation", JoinType.INNER);
          predicates.add(cb.like(cb.lower(simulationJoin.get("title")), "%" + getTitle().toLowerCase() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
