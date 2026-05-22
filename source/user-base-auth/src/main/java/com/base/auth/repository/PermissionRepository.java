package com.base.auth.repository;

import com.base.auth.model.Permission;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    Permission findFirstByName(String name);

    Boolean existsByName(String name);

    Boolean existsByAction(String action);
}
