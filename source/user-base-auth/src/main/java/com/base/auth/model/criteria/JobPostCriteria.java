package com.base.auth.model.criteria;

import com.base.auth.model.Educator;
import com.base.auth.model.JobPost;
import com.base.auth.model.Nation;
import com.base.auth.model.Simulation;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class JobPostCriteria {
  private Long id;
  private String title;
  private Long educatorId;
  private Long simulationId;
  private Long provinceId;
  private Long wardId;
  private Date date;
  private Date endDate;
  private Integer type;
  private Integer roleType;
  private Integer status;

  public Specification<JobPost> getSpecification() {
    return new Specification<JobPost>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<JobPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          predicates.add(cb.equal(root.get("id"), getId()));
        }

        if (StringUtils.isNotBlank(getTitle())) {
          predicates.add(cb.like(cb.lower(root.get("title")), "%" + getTitle() + "%"));
        }

        if (getEducatorId() != null) {
          Join<JobPost, Educator> educatorJoin = root.join("educator", JoinType.INNER);
          predicates.add(cb.equal(educatorJoin.get("id"), getEducatorId()));
        }

        if (getSimulationId() != null) {
          Join<JobPost, Simulation> simulationJoin = root.join("simulation", JoinType.INNER);
          predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));
        }

        if (getType() != null) {
          predicates.add(cb.equal(root.get("type"), getType()));
        }

        if (getRoleType() != null) {
          predicates.add(cb.equal(root.get("roleType"), getRoleType()));
        }

        if (getProvinceId() != null) {
          Join<JobPost, Nation> nationJoin = root.join("nation", JoinType.INNER);
          predicates.add(cb.equal(nationJoin.get("id"), getProvinceId()));
        }

        if (getWardId() != null) {
          Join<JobPost, Nation> nationJoin = root.join("nation", JoinType.INNER);
          predicates.add(cb.equal(nationJoin.get("id"), getWardId()));
        }

        if (getStatus() != null) {
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        if (getDate() != null) {
          Date startOfDay = DateUtils.truncate(getDate(), Calendar.DAY_OF_MONTH);
          Date endOfDay = DateUtils.addMilliseconds(DateUtils.addDays(startOfDay, 1), -1);
          predicates.add(cb.between(root.get("date"), startOfDay, endOfDay));
        }

        if (getEndDate() != null) {
          Date startOfDay = DateUtils.truncate(getEndDate(), Calendar.DAY_OF_MONTH);
          Date endOfDay = DateUtils.addMilliseconds(DateUtils.addDays(startOfDay, 1), -1);
          predicates.add(cb.between(root.get("endDate"), startOfDay, endOfDay));
        }

        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
