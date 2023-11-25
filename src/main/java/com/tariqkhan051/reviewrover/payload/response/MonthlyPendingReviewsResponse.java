package com.tariqkhan051.reviewrover.payload.response;

import java.util.List;

import com.tariqkhan051.reviewrover.models.PendingReview;

public class MonthlyPendingReviewsResponse {

    private List<PendingReview> reviews;

    public List<PendingReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<PendingReview> reviews) {
        this.reviews = reviews;
    }
}