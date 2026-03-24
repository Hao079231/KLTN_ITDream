package com.base.auth.repository;

import com.base.auth.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrganizationRepository extends JpaRepository<Organization, Long>,
    JpaSpecificationExecutor<Organization> {

  Boolean existsByNameAndShortName(String name, String shortName);
}
