package com.tariqkhan051.reviewrover.payload.request;

import javax.validation.constraints.*;

public class UpdateUserRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 50)
    @Email
    private String email;

    @Size(min = 3, max = 50)
    private String team_name;

    @Size(min = 3, max = 50)
    private String manager_name;

    @Size(min = 3, max = 50)
    private String job_name;

    @Size(min = 3, max = 50)
    private String password;

    private Boolean is_live;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIs_live() {
        return is_live;
    }

    public void setIs_live(Boolean is_live) {
        this.is_live = is_live;
    }
}
