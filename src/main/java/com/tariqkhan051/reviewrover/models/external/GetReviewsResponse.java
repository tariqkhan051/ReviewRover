package com.tariqkhan051.reviewrover.models.external;

import java.util.List;

import com.tariqkhan051.reviewrover.models.MonthlyReview;

public class GetReviewsResponse {
    private List<MonthlyReview> reviews;
    private Integer year;

    public List<MonthlyReview> getReviews() {
        return reviews;
    }
    public void setReviews(List<MonthlyReview> reviews) {
        this.reviews = reviews;
    }

    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
}


