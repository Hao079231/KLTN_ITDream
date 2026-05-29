package com.base.auth.model.criteria;

import com.base.auth.model.Simulation;
import com.base.auth.model.Task;
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
import org.springframework.data.jpa.domain.Specification;

@Data
public class TaskCriteria {
  @NotNull(message = "simulationId is required")
  private Long simulationId;
  private Integer status;
  private Integer kind;
  private Boolean isShowSimulation;

  public Specification<Task> getSpecification() {
    return new Specification<Task>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        Join<Task, Simulation> simulationJoin = root.join("simulation", JoinType.INNER);
        predicates.add(cb.equal(simulationJoin.get("id"), getSimulationId()));

        if (getKind() != null){
          predicates.add(cb.equal(root.get("kind"), getKind()));
        }

        if (getIsShowSimulation() != null){
          predicates.add(cb.equal(root.get("isShowSimulation"), getIsShowSimulation()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(simulationJoin.get("status"), getStatus()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
