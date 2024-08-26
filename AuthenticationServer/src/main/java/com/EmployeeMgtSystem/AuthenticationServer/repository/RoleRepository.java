package com.EmployeeMgtSystem.AuthenticationServer.repository;

import com.EmployeeMgtSystem.AuthenticationServer.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByNameIgnoreCase(@Param("name") String name);

}
