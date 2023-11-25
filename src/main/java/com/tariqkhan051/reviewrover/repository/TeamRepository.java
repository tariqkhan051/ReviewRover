package com.tariqkhan051.reviewrover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
  
  Optional<Team> findByName(String name);

  Boolean existsByName(String name);
}