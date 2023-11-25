package com.tariqkhan051.reviewrover.models.external;

public class GetScoresOfTeamRequest {
    public String name;
    public Integer month;
    public Integer year;
    public String review_type;
    public String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getReview_type() {
        return review_type;
    }

    public void setReview_type(String review_type) {
        this.review_type = review_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}