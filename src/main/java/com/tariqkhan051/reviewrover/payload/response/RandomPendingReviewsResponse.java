package com.tariqkhan051.reviewrover.payload.response;

import java.sql.Timestamp;

public class RandomPendingReviewsResponse {
  private Long id;

  private String user;

  private String team_name;

  private String title;

  private String description;

  private int month;

  private int year;
  
  private Timestamp created_on;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getTeam_name() {
    return team_name;
  }

  public void setTeam_name(String team_name) {
    this.team_name = team_name;
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

  public Timestamp getCreated_on() {
    return created_on;
  }

  public void setCreated_on(Timestamp created_on) {
    this.created_on = created_on;
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
}
