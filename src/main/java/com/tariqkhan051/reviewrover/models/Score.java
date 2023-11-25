package com.tariqkhan051.reviewrover.models;

public class Score {
    private User submitted_by;
    private User review_for;
    private Double score;
    private Integer month;
    private Integer year;

    public User getSubmitted_by() {
        return submitted_by;
    }

    public void setSubmitted_by(User submitted_by) {
        this.submitted_by = submitted_by;
    }

    public User getReview_for() {
        return review_for;
    }

    public void setReview_for(User review_for) {
        this.review_for = review_for;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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

}
