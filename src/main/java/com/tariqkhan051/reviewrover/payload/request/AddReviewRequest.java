package com.tariqkhan051.reviewrover.payload.request;

import javax.validation.constraints.*;

public class AddReviewRequest {
    
    @NotBlank
    private String reviewType;

    @NotBlank
    private String reviewFor;

    private String title;

    private String description;

    private int month;

    private int year;

    private Float score;

    private String goodQuality;

    private String badQuality;

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public String getReviewFor() {
        return reviewFor;
    }

    public void setReviewFor(String reviewFor) {
        this.reviewFor = reviewFor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getGoodQuality() {
        return goodQuality;
    }

    public void setGoodQuality(String goodQuality) {
        this.goodQuality = goodQuality;
    }

    public String getBadQuality() {
        return badQuality;
    }

    public void setBadQuality(String badQuality) {
        this.badQuality = badQuality;
    }
}
