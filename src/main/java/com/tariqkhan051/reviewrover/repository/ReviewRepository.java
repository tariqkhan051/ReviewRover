package com.tariqkhan051.reviewrover.repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tariqkhan051.reviewrover.models.Review;
import com.tariqkhan051.reviewrover.models.ReviewStatus;
import com.tariqkhan051.reviewrover.models.ReviewType;
import com.tariqkhan051.reviewrover.models.User;
import com.tariqkhan051.reviewrover.models.proc.Ranking;

import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

        Optional<Review> findById(Integer id);

        @Procedure("select_review_of_user")
        List<Review> getReviewsByReviewFor(String model);

        @Query(value = "CALL select_scores_of_all_users(:reviewTypeName, :status, :year, :month);", nativeQuery = true)
        List<Review> findSumOfScoresForReviewTypeAndYearAndMonth(
                        @Param("reviewTypeName") String reviewTypeName,
                        @Param("status") String status,
                        @Param("year") int year,
                        @Param("month") int month);

        @Query(value = "CALL select_scores_of_all_teams(:status, :reviewTypeName, :year, :month);", nativeQuery = true)
        List<Review> findReviewsForStatusAndReviewTypeAndYearAndMonth(@Param("status") String status,
                        @Param("reviewTypeName") String reviewTypeName,
                        @Param("year") int year,
                        @Param("month") int month);

        // @Procedure("select_scores_of_all_users(:reviewTypeName, :status, :year,
        // :month))")
        @Query(value = "select * from select_scores_of_all_users(:reviewTypeName,:status,:year,:month)", nativeQuery = true)
        List<ResultSet> getScoresOfAllUsers(
                        @Param("reviewTypeName") String reviewTypeName, @Param("status") String status,
                        @Param("year") int year, @Param("month") int month);

        @Transactional
        public int deleteBySubmittedBy(User user);

        Boolean existsBySubmittedByOrReviewFor(User submittedBy, User reviewFor);

        public List<Review> getReviewsBySubmittedBy(User submittedBy);

        public List<Review> getReviewsByReviewFor(User reviewFor);

        @Query(value = "delete from REVIEWS where review_for = :reviewFor or submitted_by = :submittedBy", nativeQuery = true)
        public int deleteReviewsBySubmittedByOrReviewFor(@Param("reviewFor") Long reviewFor,
                        @Param("submittedBy") Long submittedBy);

        public List<Review> getReviewsByStatus(ReviewStatus status);

        public List<Review> getReviewsByStatusAndReviewType(ReviewStatus status, ReviewType reviewType);

        public List<Review> getReviewsByReviewTypeAndYearAndMonthAndSubmittedByAndReviewForIn(
                        ReviewType reviewType, int year, int month, User user, List<User> users);
}