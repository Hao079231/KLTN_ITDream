package com.base.auth.model.criteria;

import com.base.auth.model.Category;
import com.base.auth.model.Organization;
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
  private Long organizationId;
  private Integer level;
  private Float avgStar;
  private Integer totalParticipant;
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

        if (getAvgStar() != null){
          predicates.add(cb.equal(root.get("avgStar"), getAvgStar()));
        }

        if (getTotalParticipant() != null){
          predicates.add(cb.equal(root.get("totalParticipant"), getTotalParticipant()));
        }

        if (getCategoryId() != null){
          Join<Simulation, Category> categoryJoin = root.join("category", JoinType.INNER);
          predicates.add(cb.equal(categoryJoin.get("id"), getCategoryId()));
        }

        if (getEducatorId() != null){
          Join<Simulation, Educator> educatorJoin = root.join("educator", JoinType.INNER);
          predicates.add(cb.equal(educatorJoin.get("id"), getEducatorId()));
        }

        if (getOrganizationId() != null){
          Join<Simulation, Educator> educatorJoin = root.join("educator", JoinType.INNER);
          Join<Educator, Organization> organizationJoin = educatorJoin.join("organization", JoinType.INNER);
          predicates.add(cb.equal(organizationJoin.get("id"), getOrganizationId()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
