package com.tariqkhan051.reviewrover.models;

import java.sql.Timestamp;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private Float score;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "submittedBy", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_submittedBy"))
    private User submittedBy;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewFor", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_reviewFor"))
    private User reviewFor;

    @Size(max = 200)
    private String goodQuality;

    @Size(max = 200)
    private String badQuality;

    @NotBlank
    @Size(max = 2)
    private Integer month;

    @NotBlank
    @Size(max = 4)
    private Integer year;

    @NotBlank
    @Column(name = "createdOn", columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE", updatable = false)
    private Timestamp createdOn;

    @NotBlank
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewTypeId", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_reviewType"))
    private ReviewType reviewType;

    @NotBlank
    @Size(max = 20)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewStatusId", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_reviewStatus"))
    private ReviewStatus status;

    @Size(max = 50)
    private String title;

    @Size(max = 100)
    private String description;

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }

    public User getReviewFor() {
        return reviewFor;
    }

    public void setReviewFor(User reviewFor) {
        this.reviewFor = reviewFor;
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

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public void setReviewType(ReviewType reviewType) {
        this.reviewType = reviewType;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
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
}
