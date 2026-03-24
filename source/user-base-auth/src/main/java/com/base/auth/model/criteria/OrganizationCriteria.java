package com.base.auth.model.criteria;

import com.base.auth.model.Organization;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class OrganizationCriteria implements Serializable{
    private static final long serialVersionUID = 1L;
    private Integer type;
    private String name;
    private String shortName;

    public Specification<Organization> getSpecification() {
        return new Specification<Organization>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(StringUtils.isNotBlank(getName())){
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
                }

                if(StringUtils.isNotBlank(getShortName())){
                    predicates.add(cb.like(cb.lower(root.get("shortName")), "%" + getShortName().toLowerCase() + "%"));
                }

                if(getType() != null){
                    predicates.add(cb.equal(root.get("type"), getType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

}
