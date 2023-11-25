package com.tariqkhan051.reviewrover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.ERole;
import com.tariqkhan051.reviewrover.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
