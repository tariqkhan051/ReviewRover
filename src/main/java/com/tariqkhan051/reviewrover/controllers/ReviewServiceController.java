package com.tariqkhan051.reviewrover.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tariqkhan051.reviewrover.DbHandler;
import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.helpers.Messages.ResponseMessages;
import com.tariqkhan051.reviewrover.helpers.Validator.GetReviewsRequestValidator;
import com.tariqkhan051.reviewrover.helpers.Validator.ReviewValidator;
import com.tariqkhan051.reviewrover.models.EReviewStatus;
import com.tariqkhan051.reviewrover.models.EReviewType;
import com.tariqkhan051.reviewrover.models.PendingReview;
import com.tariqkhan051.reviewrover.models.Review;
import com.tariqkhan051.reviewrover.models.ReviewStatus;
import com.tariqkhan051.reviewrover.models.ReviewType;
import com.tariqkhan051.reviewrover.models.User;
import com.tariqkhan051.reviewrover.models.external.GetReviewsRequest;
import com.tariqkhan051.reviewrover.payload.response.MonthlyPendingReviewsResponse;
import com.tariqkhan051.reviewrover.payload.response.MessageResponse;
import com.tariqkhan051.reviewrover.payload.response.RandomPendingReviewsResponse;
import com.tariqkhan051.reviewrover.repository.ReviewRepository;
import com.tariqkhan051.reviewrover.repository.ReviewStatusRepository;
import com.tariqkhan051.reviewrover.repository.ReviewTypeRepository;
import com.tariqkhan051.reviewrover.repository.UserRepository;
import com.tariqkhan051.reviewrover.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

import com.tariqkhan051.reviewrover.payload.request.AddReviewRequest;
import com.tariqkhan051.reviewrover.payload.request.GetRankingRequest;
import com.tariqkhan051.reviewrover.payload.request.UpdateReviewRequest;
import com.tariqkhan051.reviewrover.converters.*;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class ReviewServiceController {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewTypeRepository reviewTypeRepository;

    @Autowired
    ReviewStatusRepository reviewStatusRepository;

    @CrossOrigin
    @RequestMapping(value = "/reviews", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> addReview(@Valid @RequestBody AddReviewRequest createReviewRequest) {
        try {

            // Validations
            if (!ReviewValidator.IsValidDateRange(createReviewRequest.getMonth(),
                    createReviewRequest.getYear())) {
                return ResponseMessages.ErrorResponse("Invalid month or year.");
            }

            // DbHandler dbHandler = new DbHandler();
            // if (dbHandler.InsertReview(createReviewRequest)) {
            // return ResponseMessages.SuccessResponse("Review submitted successfully.");
            // }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof AnonymousAuthenticationToken)
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            var submittedBy = userRepository.findById(userId);

            if (!submittedBy.isPresent()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: user not found!"));
            }

            if (!submittedBy.get().getIsLive()) {
                return ResponseEntity.badRequest().body(new MessageResponse(ResponseMessages.USER_IS_NOT_LIVE));
            }

            var reviewForUser = userRepository.findByUsername(createReviewRequest.getReviewFor());

            if (!reviewForUser.isPresent()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: user not found!"));
            }

            if (!reviewForUser.get().getIsLive()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse(ResponseMessages.REVIEW_FOR_USER_IS_NOT_LIVE));
            }

            Review review = new Review();
            review.setScore(createReviewRequest.getScore());
            review.setSubmittedBy(submittedBy.get());
            review.setReviewFor(reviewForUser.get());
            review.setGoodQuality(Utils.SafeTrim(createReviewRequest.getGoodQuality()));
            review.setBadQuality(Utils.SafeTrim(createReviewRequest.getBadQuality()));
            review.setMonth(createReviewRequest.getMonth());
            review.setYear(createReviewRequest.getYear());

            EReviewType reviewType = Convert.getReviewType(createReviewRequest.getReviewType());
            EReviewStatus reviewStatus = Convert.getReviewStatusForReviewType(reviewType);

            // insert review type if does not exist in database
            if (!reviewTypeRepository.existsByName(reviewType)) {
                reviewTypeRepository.save(new ReviewType(reviewType));
            }

            // insert review status if does not exist in database
            if (!reviewStatusRepository.existsByName(reviewStatus)) {
                reviewStatusRepository.save(new ReviewStatus(reviewStatus));
            }

            review.setReviewType(reviewTypeRepository.findByName(reviewType).get());
            review.setStatus(reviewStatusRepository.findByName(reviewStatus).get());
            review.setCreatedOn(Utils.GetCurrentTimeStamp());
            review.setTitle(createReviewRequest.getTitle());
            review.setDescription(createReviewRequest.getDescription());
            reviewRepository.save(review);

            return ResponseMessages.SuccessResponse("Review submitted successfully.");
        } catch (Exception exception) {
            // System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to add review.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateReviewStatus(
            @Valid @RequestBody List<UpdateReviewRequest> updateReviewRequest) {
        try {
            if (updateReviewRequest != null && updateReviewRequest.size() > 0) {
                if (updateReviewRequest.size() == 1) {
                    UpdateReviewRequest request = updateReviewRequest.get(0);
                    if (request != null) {
                        if (request.getId() > 0 || request.getStatus() != null) {
                            var review = reviewRepository.findById(request.getId());
                            if (!review.isPresent()) {
                                return ResponseMessages.ErrorResponse("No review found against the id.");
                            }
                            EReviewStatus reviewStatus = request.getStatus();
                            ReviewStatus status = reviewStatusRepository.findByName(reviewStatus).get();
                            review.get().setStatus(status);
                            reviewRepository.save(review.get());
                            return ResponseMessages.SuccessResponse("Review status updated successfully.");
                        }
                    }
                } else {
                    Boolean isAnyReviewUpdated = false;
                    for (UpdateReviewRequest request : updateReviewRequest) {
                        if (request != null) {
                            if (request.getId() > 0 || request.getStatus() != null) {
                                var review = reviewRepository.findById(request.getId());
                                if (!review.isPresent()) {
                                    continue;
                                }
                                EReviewStatus reviewStatus = request.getStatus();
                                ReviewStatus status = reviewStatusRepository.findByName(reviewStatus).get();
                                review.get().setStatus(status);
                                reviewRepository.save(review.get());
                                isAnyReviewUpdated = true;
                            }
                        }
                    }
                    if (isAnyReviewUpdated) {
                        return ResponseMessages.SuccessResponse("Reviews status updated successfully.");
                    }
                    return ResponseMessages.SuccessResponse("No reviews status updated.");
                }
            }
            return ResponseMessages.ErrorResponse("Invalid request. Please provide id and status.");
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to update review status.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/manager", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> updateReviewStatusForManager(
            @Valid @RequestBody List<UpdateReviewRequest> updateReviewRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof AnonymousAuthenticationToken) {
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));
            }
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();
            if (!userRepository.existsByUsername(username)) {
                return ResponseMessages.ErrorResponse("User not found.");
            }
            User user = userRepository.findByUsername(username).get();
            if (!userRepository.existsByManagerId(user.getId())) {
                return ResponseMessages.ErrorResponse("You are not allowed to access the required resource.");
            }

            if (updateReviewRequest != null && updateReviewRequest.size() > 0) {
                if (updateReviewRequest.size() == 1) {
                    UpdateReviewRequest request = updateReviewRequest.get(0);
                    if (request != null) {
                        if (request.getId() > 0 || request.getStatus() != null) {
                            var review = reviewRepository.findById(request.getId());
                            if (!review.isPresent()) {
                                return ResponseMessages.ErrorResponse("No review found against the id.");
                            }
                            if (review.get().getReviewFor().getManager() == null ||
                                    review.get().getReviewFor().getManager().getId() != user.getId()) {
                                return ResponseMessages.ErrorResponse("You are not allowed to update the status.");
                            }
                            EReviewStatus reviewStatus = request.getStatus();
                            ReviewStatus status = reviewStatusRepository.findByName(reviewStatus).get();
                            review.get().setStatus(status);
                            reviewRepository.save(review.get());
                            return ResponseMessages.SuccessResponse("Review status updated successfully.");
                        }
                    }
                } else {
                    Boolean isAnyReviewUpdated = false;
                    for (UpdateReviewRequest request : updateReviewRequest) {
                        if (request != null) {
                            if (request.getId() > 0 || request.getStatus() != null) {
                                var review = reviewRepository.findById(request.getId());
                                if (!review.isPresent()) {
                                    continue;
                                }
                                if (review.get().getReviewFor().getManager() == null ||
                                        review.get().getReviewFor().getManager().getId() != user.getId()) {
                                    continue;
                                }
                                EReviewStatus reviewStatus = request.getStatus();
                                ReviewStatus status = reviewStatusRepository.findByName(reviewStatus).get();
                                review.get().setStatus(status);
                                reviewRepository.save(review.get());
                                isAnyReviewUpdated = true;
                            }
                        }
                    }
                    if (isAnyReviewUpdated) {
                        return ResponseMessages.SuccessResponse("Reviews status updated successfully.");
                    }
                    return ResponseMessages.SuccessResponse("No reviews status updated.");
                }
            }
            return ResponseMessages.ErrorResponse("Invalid request. Please provide id and status.");
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to update review status.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/pending/user", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> getMonthlyPendingReviewsForUser(@RequestParam Integer year,
            @RequestParam Integer month) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof AnonymousAuthenticationToken)
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();

            if (!userRepository.existsByUsername(username)) {
                return ResponseMessages.ErrorResponse("User not found.");
            }

            User user = userRepository.findByUsername(username).get();

            if (user.getTeam() == null) {
                return ResponseMessages.ErrorResponse("You are not assigned to any team.");
            }

            List<User> teamUsers = userRepository.findByTeamAndIsLive(user.getTeam(), true);

            // filter self from the list
            teamUsers = teamUsers.stream().filter(u -> u.getId() != user.getId()).collect(Collectors.toList());

            if (teamUsers.size() == 0) {
                return ResponseMessages.ErrorResponse("No live users found in your team.");
            }

            // get monthly approved reviews for the month year submitted by user for the
            // team users
            List<Review> reviews = reviewRepository
                    .getReviewsByReviewTypeAndYearAndMonthAndSubmittedByAndReviewForIn(
                            reviewTypeRepository.findByName(EReviewType.MONTHLY).get(),
                            year, month,
                            user,
                            teamUsers);

            if (reviews.size() > 0) {

                // filter users who have not submitted the review
                teamUsers = teamUsers.stream()
                        .filter(u -> !reviews.stream().anyMatch(r -> r.getReviewFor().getId() == u.getId()))
                        .collect(Collectors.toList());
            }

            MonthlyPendingReviewsResponse getPendingReviewsResponse = new MonthlyPendingReviewsResponse();

            if (teamUsers.size() > 0) {
                List<PendingReview> pendingReviews = new ArrayList<PendingReview>();

                for (User teamUser : teamUsers) {
                    PendingReview pendingReview = new PendingReview();
                    pendingReview.setUser(teamUser.getName());

                    if (teamUser.getUsername() != null) {
                        pendingReview.setUsername(teamUser.getUsername());
                    }
                    if (teamUser.getJob() != null) {
                        pendingReview.setJob_name(teamUser.getJob().getName());
                    }

                    pendingReviews.add(pendingReview);
                }

                getPendingReviewsResponse.setReviews(pendingReviews);
                return ResponseMessages.SuccessResponseData(
                        getPendingReviewsResponse, "Pending reviews retrieved successfully.");
            }

            return ResponseMessages.SuccessResponse("No pending reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get pending reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/pending", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getReviewsPendingApproval() {
        try {
            var reviews = reviewRepository
                    .getReviewsByStatus(reviewStatusRepository.findByName(EReviewStatus.PENDING).get());

            if (reviews.size() > 0) {
                List<RandomPendingReviewsResponse> pendingReviews = new ArrayList<RandomPendingReviewsResponse>();

                for (Review review : reviews) {
                    RandomPendingReviewsResponse pendingReview = new RandomPendingReviewsResponse();
                    pendingReview.setId(review.getId());
                    pendingReview.setUser(review.getReviewFor().getName());

                    if (review.getReviewFor().getTeam() != null) {
                        pendingReview.setTeam_name(review.getReviewFor().getTeam().getName());
                    }

                    pendingReview.setMonth(review.getMonth());
                    pendingReview.setYear(review.getYear());
                    pendingReview.setCreated_on(review.getCreatedOn());
                    pendingReview.setTitle(review.getTitle());
                    pendingReview.setDescription(review.getDescription());
                    pendingReviews.add(pendingReview);
                }

                return ResponseMessages.SuccessResponseData(
                        pendingReviews,
                        "Pending reviews retrieved successfully.");

            }

            return ResponseMessages.SuccessResponse("No pending reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get pending reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/approved", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getReviewsApproved() {
        try {
            var reviews = reviewRepository
                    .getReviewsByStatusAndReviewType(reviewStatusRepository.findByName(EReviewStatus.APPROVED).get(),
                            reviewTypeRepository.findByName(EReviewType.RANDOM).get());

            if (reviews.size() > 0) {
                List<RandomPendingReviewsResponse> randomReviews = new ArrayList<RandomPendingReviewsResponse>();

                for (Review review : reviews) {
                    RandomPendingReviewsResponse pendingReview = new RandomPendingReviewsResponse();
                    pendingReview.setId(review.getId());
                    pendingReview.setUser(review.getReviewFor().getName());

                    if (review.getReviewFor().getTeam() != null) {
                        pendingReview.setTeam_name(review.getReviewFor().getTeam().getName());
                    }

                    pendingReview.setMonth(review.getMonth());
                    pendingReview.setYear(review.getYear());
                    pendingReview.setCreated_on(review.getCreatedOn());
                    pendingReview.setTitle(review.getTitle());
                    pendingReview.setDescription(review.getDescription());
                    randomReviews.add(pendingReview);
                }

                return ResponseMessages.SuccessResponseData(
                        randomReviews,
                        "Approved random reviews retrieved successfully.");

            }

            return ResponseMessages.SuccessResponse("No approved random reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get approved random reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/rejected", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getReviewsRejected() {
        try {
            var reviews = reviewRepository
                    .getReviewsByStatusAndReviewType(reviewStatusRepository.findByName(EReviewStatus.REJECTED).get(),
                            reviewTypeRepository.findByName(EReviewType.RANDOM).get());

            if (reviews.size() > 0) {
                List<RandomPendingReviewsResponse> randomReviews = new ArrayList<RandomPendingReviewsResponse>();

                for (Review review : reviews) {
                    RandomPendingReviewsResponse pendingReview = new RandomPendingReviewsResponse();
                    pendingReview.setId(review.getId());
                    pendingReview.setUser(review.getReviewFor().getName());

                    if (review.getReviewFor().getTeam() != null) {
                        pendingReview.setTeam_name(review.getReviewFor().getTeam().getName());
                    }

                    pendingReview.setMonth(review.getMonth());
                    pendingReview.setYear(review.getYear());
                    pendingReview.setCreated_on(review.getCreatedOn());
                    pendingReview.setTitle(review.getTitle());
                    pendingReview.setDescription(review.getDescription());
                    randomReviews.add(pendingReview);
                }

                return ResponseMessages.SuccessResponseData(
                        randomReviews,
                        "Rejected random reviews retrieved successfully.");

            }

            return ResponseMessages.SuccessResponse("No rejected random reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get rejected random reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/pending/manager", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> getReviewsPendingApprovalForManager() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof AnonymousAuthenticationToken)
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();

            if (!userRepository.existsByUsername(username)) {
                return ResponseMessages.ErrorResponse("User not found.");
            }

            User user = userRepository.findByUsername(username).get();

            if (!userRepository.existsByManagerId(user.getId())) {
                return ResponseMessages.ErrorResponse("You are not allowed to access the required resource.");
            }

            var reviews = reviewRepository
                    .getReviewsByStatus(reviewStatusRepository.findByName(EReviewStatus.PENDING).get());

            if (reviews.size() > 0) {
                List<RandomPendingReviewsResponse> pendingReviews = new ArrayList<RandomPendingReviewsResponse>();

                for (Review review : reviews) {
                    RandomPendingReviewsResponse pendingReview = new RandomPendingReviewsResponse();

                    // removed same team check since manager can be of different team
                    /// user.getTeam().equals(review.getReviewFor().getTeam()) &&
                    if ((review.getReviewFor().getManager() != null &&
                            user.getId().equals(review.getReviewFor().getManager().getId()))) {

                        pendingReview.setId(review.getId());
                        pendingReview.setUser(review.getReviewFor().getName());

                        if (review.getReviewFor().getTeam() != null) {

                            pendingReview.setTeam_name(review.getReviewFor().getTeam().getName());

                        }

                        pendingReview.setMonth(review.getMonth());
                        pendingReview.setYear(review.getYear());
                        pendingReview.setCreated_on(review.getCreatedOn());
                        pendingReview.setTitle(review.getTitle());
                        pendingReview.setDescription(review.getDescription());
                        pendingReviews.add(pendingReview);
                    }
                }

                if (pendingReviews.size() > 0) {
                    return ResponseMessages.SuccessResponseData(
                            pendingReviews,
                            "Pending reviews retrieved successfully.");
                }
            }

            return ResponseMessages.SuccessResponse("No pending reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get pending reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/approved/manager", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> getReviewsApprovedForManager() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof AnonymousAuthenticationToken)
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();

            if (!userRepository.existsByUsername(username)) {
                return ResponseMessages.ErrorResponse("User not found.");
            }

            User user = userRepository.findByUsername(username).get();

            if (!userRepository.existsByManagerId(user.getId())) {
                return ResponseMessages.ErrorResponse("You are not allowed to access the required resource.");
            }

            var reviews = reviewRepository
                    .getReviewsByStatusAndReviewType(reviewStatusRepository.findByName(EReviewStatus.APPROVED).get(),
                            reviewTypeRepository.findByName(EReviewType.RANDOM).get());

            if (reviews.size() > 0) {
                List<RandomPendingReviewsResponse> randomReviews = new ArrayList<RandomPendingReviewsResponse>();

                for (Review review : reviews) {
                    RandomPendingReviewsResponse pendingReview = new RandomPendingReviewsResponse();

                    // removed same team check since manager can be of different team
                    /// user.getTeam().equals(review.getReviewFor().getTeam()) &&
                    if ((review.getReviewFor().getManager() != null &&
                            user.getId().equals(review.getReviewFor().getManager().getId()))) {

                        pendingReview.setId(review.getId());
                        pendingReview.setUser(review.getReviewFor().getName());

                        if (review.getReviewFor().getTeam() != null) {

                            pendingReview.setTeam_name(review.getReviewFor().getTeam().getName());

                        }

                        pendingReview.setMonth(review.getMonth());
                        pendingReview.setYear(review.getYear());
                        pendingReview.setCreated_on(review.getCreatedOn());
                        pendingReview.setTitle(review.getTitle());
                        pendingReview.setDescription(review.getDescription());
                        randomReviews.add(pendingReview);
                    }
                }

                if (randomReviews.size() > 0) {
                    return ResponseMessages.SuccessResponseData(
                            randomReviews,
                            "Approved random reviews retrieved successfully.");
                }
            }

            return ResponseMessages.SuccessResponse("No approved random reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get approved random reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/rejected/manager", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Object> getReviewsRejectedForManager() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof AnonymousAuthenticationToken)
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();

            if (!userRepository.existsByUsername(username)) {
                return ResponseMessages.ErrorResponse("User not found.");
            }

            User user = userRepository.findByUsername(username).get();

            if (!userRepository.existsByManagerId(user.getId())) {
                return ResponseMessages.ErrorResponse("You are not allowed to access the required resource.");
            }

            var reviews = reviewRepository
                    .getReviewsByStatusAndReviewType(reviewStatusRepository.findByName(EReviewStatus.REJECTED).get(),
                            reviewTypeRepository.findByName(EReviewType.RANDOM).get());

            if (reviews.size() > 0) {
                List<RandomPendingReviewsResponse> randomReviews = new ArrayList<RandomPendingReviewsResponse>();

                for (Review review : reviews) {
                    RandomPendingReviewsResponse pendingReview = new RandomPendingReviewsResponse();

                    // removed same team check since manager can be of different team
                    /// user.getTeam().equals(review.getReviewFor().getTeam()) &&
                    if ((review.getReviewFor().getManager() != null &&
                            user.getId().equals(review.getReviewFor().getManager().getId()))) {

                        pendingReview.setId(review.getId());
                        pendingReview.setUser(review.getReviewFor().getName());

                        if (review.getReviewFor().getTeam() != null) {

                            pendingReview.setTeam_name(review.getReviewFor().getTeam().getName());

                        }

                        pendingReview.setMonth(review.getMonth());
                        pendingReview.setYear(review.getYear());
                        pendingReview.setCreated_on(review.getCreatedOn());
                        pendingReview.setTitle(review.getTitle());
                        pendingReview.setDescription(review.getDescription());
                        randomReviews.add(pendingReview);
                    }
                }

                if (randomReviews.size() > 0) {
                    return ResponseMessages.SuccessResponseData(
                            randomReviews,
                            "Approved random reviews retrieved successfully.");
                }
            }

            return ResponseMessages.SuccessResponse("No rejected random reviews found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get rejected random reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/ranking", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getRanking(@Valid @RequestBody GetRankingRequest request) {
        try {
            DbHandler dbHandler = new DbHandler();
            var rankingResponse = dbHandler.GetRankingOfAllUsers(request);
            // var result = reviewRepository.getScoresOfAllUsers(
            // reviewType,
            // EReviewStatus.APPROVED.toString(),
            // request.getYear(),
            // request.getMonth());

            if (rankingResponse.size() > 0) {
                // rankingResponse.sort(Comparator.comparing(RankingResponse::getScore).reversed());

                return ResponseMessages.SuccessResponseData(
                        rankingResponse,
                        "Ranking retrieved successfully.");
            }

            return ResponseMessages.SuccessResponse("No ranks found.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to get reviews.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> getReviews(
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> team,
            @RequestParam Optional<String> user,
            @RequestParam Optional<Integer> year, @RequestParam Optional<Integer> month,
            @RequestParam Optional<String> status) {

        if (!GetReviewsRequestValidator.IsValidRequest(type, team, user, year, month, status)) {
            return ResponseMessages.MissingFieldsResponse();
        }

        GetReviewsRequest request = new GetReviewsRequest();
        request.setUser(user);
        request.setMonth(month);
        request.setTeam(team);
        request.setType(type);
        request.setYear(year);
        request.setStatus(status);

        try {
            DbHandler dbHandler = new DbHandler();

            if (status.isPresent()) {

                if (user.isPresent() && month.isPresent() && year.isPresent()) {

                    return ResponseMessages.SuccessResponseData(dbHandler.GetReviews(request),
                            "Reviews retrieved successfully for the user.");
                }

                return ResponseMessages.SuccessResponseData(dbHandler.GetPendingReviews(request, !user.isPresent()),
                        "Pending reviews retrieved successfully.");
            }

            if (user.isPresent()) {
                return ResponseMessages.SuccessResponseData(dbHandler.GetReviewsOfUser(request),
                        "Reviews retrieved successfully for the user.");
            }

            if (team.isPresent()) {
                return ResponseMessages.SuccessResponseData(dbHandler.GetReviewsOfTeam(request),
                        "Reviews retrieved successfully for the team.");
            }

            // List<Review> reviews =
            // reviewRepository.findReviewsForStatusAndReviewTypeAndYearAndMonth(
            // request.getStatus(),
            // request.getType(),
            // request.getYear(),
            // request.getMonth());

            // return ResponseMessages.SuccessResponseData(
            // reviews,
            // "Reviews retrieved successfully.");

            return ResponseMessages.SuccessResponseData(dbHandler.GetReviewsOfAllTeams(request),
                    "Reviews retrieved successfully.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse(exception.getMessage());
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/reviews/departments", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> getReviewsOfDepartments(
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> department,
            @RequestParam Optional<Integer> year, @RequestParam Optional<Integer> month,
            @RequestParam Optional<String> status) {

        GetReviewsRequest request = new GetReviewsRequest();
        request.setMonth(month);
        request.setType(type);
        request.setYear(year);
        request.setStatus(status);
        request.setDepartment(department);

        try {
            DbHandler dbHandler = new DbHandler();

            return ResponseMessages.SuccessResponseData(dbHandler.GetReviewsOfAllDepartments(request),
                    "Reviews retrieved successfully.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseMessages.ExceptionResponse("Unable to retrieve reviews.");
        }
    }

}