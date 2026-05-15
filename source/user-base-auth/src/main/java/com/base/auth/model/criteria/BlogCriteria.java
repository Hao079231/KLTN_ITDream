package com.base.auth.model.criteria;

import com.base.auth.model.Blog;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class BlogCriteria {
  private Long educatorId;
  private Long parentId;
  private Long categoryId;
  private Integer status;

  public Specification<Blog> getSpecification() {
    return new Specification<Blog>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(
          Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (getEducatorId() != null){
          predicates.add(cb.equal(root.get("educator").get("id"), getEducatorId()));
        }

        if (getCategoryId() != null){
          predicates.add(cb.equal(root.get("category").get("id"), getCategoryId()));
        }

        if (getParentId() != null){
          predicates.add(cb.equal(root.get("parent").get("id"), getParentId()));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
