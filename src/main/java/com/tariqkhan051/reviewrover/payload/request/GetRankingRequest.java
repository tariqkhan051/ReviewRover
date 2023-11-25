package com.tariqkhan051.reviewrover.payload.request;

import com.tariqkhan051.reviewrover.models.EReviewType;

public class GetRankingRequest {
    
    private EReviewType review_type;

    public EReviewType getReview_type() {
        return review_type;
    }

    public void setReview_type(EReviewType reviewType) {
        this.review_type = reviewType;
    }

    private int month;

    private int year;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
