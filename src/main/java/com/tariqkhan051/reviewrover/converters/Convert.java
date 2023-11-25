package com.tariqkhan051.reviewrover.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tariqkhan051.reviewrover.models.DepartmentScore;
import com.tariqkhan051.reviewrover.models.EReviewStatus;
import com.tariqkhan051.reviewrover.models.EReviewType;
import com.tariqkhan051.reviewrover.models.MonthlyReview;
import com.tariqkhan051.reviewrover.models.TeamScore;
import com.tariqkhan051.reviewrover.models.UserScore;

public class Convert {
    public static List<MonthlyReview> getMonthlyReviews(HashMap<Integer, List<UserScore>> reviews) {
        List<MonthlyReview> monthlyReviews = new ArrayList<MonthlyReview>();
        for (Integer month : reviews.keySet()) {
            MonthlyReview monthlyReview = new MonthlyReview();
            monthlyReview.setMonth(month);
            monthlyReview.setUsers(reviews.get(month));
            monthlyReviews.add(monthlyReview);
        }
        return monthlyReviews;
    }

    public static List<MonthlyReview> getMonthlyReviewsOfTeams(HashMap<Integer, List<TeamScore>> reviews) {
        List<MonthlyReview> monthlyReviews = new ArrayList<MonthlyReview>();
        for (Integer month : reviews.keySet()) {
            MonthlyReview monthlyReview = new MonthlyReview();
            monthlyReview.setMonth(month);
            monthlyReview.setTeams(reviews.get(month));
            monthlyReviews.add(monthlyReview);
        }
        return monthlyReviews;
    }

    public static List<MonthlyReview> getMonthlyReviewsOfDepartments(HashMap<Integer, List<DepartmentScore>> reviews) {
        List<MonthlyReview> monthlyReviews = new ArrayList<MonthlyReview>();
        for (Integer month : reviews.keySet()) {
            MonthlyReview monthlyReview = new MonthlyReview();
            monthlyReview.setMonth(month);
            monthlyReview.setDepartments(reviews.get(month));
            monthlyReviews.add(monthlyReview);
        }
        return monthlyReviews;
    }

    public static EReviewType getReviewType(String reviewType) {
        if (reviewType == null || reviewType.isEmpty())
            return EReviewType.MONTHLY;

        switch (reviewType.toLowerCase()) {
            case "monthly":
                return EReviewType.MONTHLY;
            case "random":
                return EReviewType.RANDOM;
            default:
                return EReviewType.MONTHLY;
        }
    }

    public static EReviewStatus getReviewStatusForReviewType(EReviewType reviewType) {

        if (reviewType.equals(EReviewType.MONTHLY)) {
            return EReviewStatus.APPROVED;
        }

        if (reviewType.equals(EReviewType.RANDOM)) {
            return EReviewStatus.PENDING;
        }

        return EReviewStatus.PENDING;
    }
}