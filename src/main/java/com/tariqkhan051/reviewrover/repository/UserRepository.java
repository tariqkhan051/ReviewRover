package com.tariqkhan051.reviewrover.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.Team;
import com.tariqkhan051.reviewrover.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByManagerId(Long managerId);

  public List<User> findAllByOrderByCreatedOnDesc();

  public List<User> findByTeam(Team team);

  public List<User> findByTeamAndIsLive(Team team, boolean isLive);

  public int deleteByUsername(String username);

  // @Query
  // public List<User> findUsersByTeamAndIsLiveWithNoReviewsSubmittedByAndMonthAndYearAndReviewType(Long teamId,
  //     Boolean isLive, Long userId, int month, int year, ReviewType reviewType);
}
