package com.tariqkhan051.reviewrover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.EReviewStatus;
import com.tariqkhan051.reviewrover.models.ReviewStatus;
import com.tariqkhan051.reviewrover.models.ReviewType;

@Repository
public interface ReviewStatusRepository extends JpaRepository<ReviewStatus, Long> {
  
  Boolean existsByName(EReviewStatus reviewStatus);

  Optional<ReviewStatus> findByName(EReviewStatus reviewType);

  Optional<ReviewType> findById(Integer id);
}