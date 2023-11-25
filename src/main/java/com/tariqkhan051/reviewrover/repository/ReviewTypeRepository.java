package com.tariqkhan051.reviewrover.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.EReviewType;
import com.tariqkhan051.reviewrover.models.ReviewType;

@Repository
public interface ReviewTypeRepository extends JpaRepository<ReviewType, Long> {
  
  Boolean existsByName(EReviewType reviewType);
  
  Optional<ReviewType> findByName(EReviewType reviewType);

  Optional<ReviewType> findById(Integer id);
}