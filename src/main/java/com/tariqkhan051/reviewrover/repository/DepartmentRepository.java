package com.tariqkhan051.reviewrover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
  
  Optional<Department> findByName(String name);

  Boolean existsByName(String name);
}