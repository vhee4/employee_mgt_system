package com.EmployeeMgtSystem.AuthenticationServer.repository;

import com.EmployeeMgtSystem.AuthenticationServer.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Integer> {
    Optional<Permission> findByName(String name);
}
