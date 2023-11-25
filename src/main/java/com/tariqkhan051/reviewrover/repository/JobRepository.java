package com.tariqkhan051.reviewrover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
  
  Optional<Job> findByName(String name);

  Optional<Job> findById(Integer id);

  Boolean existsByName(String name);
}