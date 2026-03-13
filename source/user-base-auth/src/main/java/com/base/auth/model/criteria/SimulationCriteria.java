package com.base.auth.model.criteria;

import com.base.auth.model.Category;
import com.base.auth.model.Simulation;
import com.base.auth.model.Educator;
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
public class SimulationCriteria {
  private String title;
  private Long categoryId;
  private Long educatorId;
  private Integer level;
  private Integer status;

  public Specification<Simulation> getSpecification() {
    return new Specification<Simulation>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Simulation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.isNotBlank(getTitle()))
        {
          predicates.add(cb.like(cb.lower(root.get("title")),"%"+ getTitle()+"%"));
        }

        if (getLevel() != null){
          predicates.add(cb.equal(root.get("level"), getLevel()));
        }

        if (getCategoryId() != null){
          Join<Simulation, Category> categoryJoin = root.join("category", JoinType.INNER);
          predicates.add(cb.equal(categoryJoin.get("id"), getCategoryId()));
        }

        if (getEducatorId() != null){
          Join<Simulation, Educator> educatorJoin = root.join("educator", JoinType.INNER);
          predicates.add(cb.equal(educatorJoin.get("id"), getEducatorId()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
