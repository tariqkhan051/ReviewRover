package com.tariqkhan051.reviewrover.helpers;

import java.util.Optional;

import com.tariqkhan051.reviewrover.models.Score;
import com.tariqkhan051.reviewrover.models.Team;
import com.tariqkhan051.reviewrover.models.User;
import com.tariqkhan051.reviewrover.models.external.GetScoresOfTeamRequest;
import com.tariqkhan051.reviewrover.models.external.GetUserReviewsRequest;

public class Validator {

    public static class TeamValidator {
        public static boolean IsValidTeam(Team team) {
            if (team == null || Utils.IsNullOrEmpty(team.getName())) {
                return false;
            }

            return true;
        }

        public static boolean IsValidRequest(Team team) {
            if (team == null || Utils.IsNullOrEmpty(team.getName())) {
                return false;
            }

            return true;
        }
    }

    public static class UserValidator {
        public static boolean IsValidUser(User user) {
            if (user == null || Utils.IsNullOrEmpty(user.getName()) || 
            Utils.IsNullOrEmpty(user.getUsername())) {
                return false;
            }
            return true;
        }

        public static boolean IsAuthenticatedUser(User user) {
            if (user == null || Utils.IsNullOrEmpty(user.getUsername()) ||
                    Utils.IsNullOrEmpty(user.getPassword())) {
                return false;
            }
            return true;
        }

        public static boolean IsValidRequest(User user) {
            if (user == null || Utils.IsNullOrEmpty(user.getUsername()) ||
                    Utils.IsNullOrEmpty(user.getPassword())) {
                return false;
            }

            return true;
        }
    }

    public static class ReviewValidator {
        // public static boolean IsValidReview(Review review) {
        //     if (review == null ||
        //             review.getReviewFor() == null ||
        //             Utils.IsNullOrEmpty(review.getReviewFor().getUsername()) ||
        //             review.getReviewType() == null ||
        //             Utils.IsNullOrEmpty(review.getReviewType().getName().toString())) {
        //         return false;
        //     }

        //     if (review.getReviewType().getName().equals(EReviewType.RANDOM) && Utils.IsNullOrEmpty(review.getTitle())) {
        //         return false;
        //     }
            
        //     return true;
        // }

        public static boolean IsValidDateRange(int month, int year) {
            return (Utils.IsValidMonth(month) && Utils.IsValidYear(year)) && 
            year <= Utils.getCurrentYear() && 
            (year == Utils.getCurrentYear() && month < Utils.getCurrentMonth());
        }
    }

    public static class ScoreValidator {
        public static boolean IsValidScore(Score score) {
            if (score == null ||
                    score.getReview_for() == null ||
                    Utils.IsNullOrEmpty(score.getReview_for().getName()) ||
                    !Utils.IsValidMonth(score.getMonth()) ||
                    !Utils.IsValidYear(score.getYear())) {
                return false;
            }

            return true;
        }
    }

    public static class GetReviewsRequestValidator {
        public static boolean IsValidRequest(Optional<String> type, Optional<String> team, Optional<String> user,
                Optional<Integer> year,
                Optional<Integer> month,
                Optional<String> status) {

            var isYearProvided = year.isPresent() && year.get() != null;
            var isMonthProvided = month.isPresent() && month.get() != null;

            if (isMonthProvided && !Utils.IsValidMonth(month.get())) {
                return false;
            }

            return true;
        }
    }

    public static class GetUserReviewsRequestValidator {
        public static boolean IsValidRequest(GetUserReviewsRequest request) {
            if (request == null || Utils.IsNullOrEmpty(request.getName())) {
                return false;
            }

            if (Utils.IsNullOrEmpty(request.getReview_type())) {
                return false;
            }

            if (!Utils.IsValidMonth(request.getMonth())) {
                return false;
            }

            if (!Utils.IsValidYear(request.getYear())) {
                return false;
            }

            return true;
        }
    }

    public static class GetScoresOfTeamRequestValidator {
        public static boolean IsValidRequest(GetScoresOfTeamRequest request) {
            if (request == null || Utils.IsNullOrEmpty(request.getName())) {
                return false;
            }

            if (request.getMonth() != null && !Utils.IsValidMonth(request.getMonth())) {
                return false;
            }

            if (request.getYear() != null && !Utils.IsValidYear(request.getYear())) {
                return false;
            }

            return true;
        }
    }
}