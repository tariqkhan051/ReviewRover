package com.tariqkhan051.reviewrover.payload.response;

public class UserDetailsResponse {

  private String name;

  private String team_name;

  private Boolean is_manager;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getIs_manager() {
    return is_manager;
  }

  public void setIs_manager(Boolean is_manager) {
    this.is_manager = is_manager;
  }

  public String getTeam_name() {
    return team_name;
  }

  public void setTeam_name(String team_name) {
    this.team_name = team_name;
  }

}
