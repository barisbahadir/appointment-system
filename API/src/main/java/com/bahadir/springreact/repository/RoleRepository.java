package com.bahadir.springreact.repository;

import com.bahadir.springreact.model.Role;
import com.bahadir.springreact.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName roleName);

}
