package com.tariqkhan051.reviewrover.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tariqkhan051.reviewrover.helpers.Utils;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonthlyReview {
    private Integer month;
    private Double score;
    private List<String> good_quality;
    private List<String> bad_quality;

    private List<UserScore> users;
    private List<TeamScore> teams;
    private List<DepartmentScore> departments;

    public List<DepartmentScore> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentScore> departments) {
        this.departments = departments;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<UserScore> getUsers() {
        return users;
    }

    public void setUsers(List<UserScore> users) {
        this.users = users;
    }

    public List<TeamScore> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamScore> teams) {
        this.teams = teams;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<String> getGood_quality() {
        return good_quality;
    }

    public void setGood_quality(List<String> good_quality) {
        this.good_quality = good_quality;
    }

    public void setGood_quality(String good_quality) {
        if (!Utils.IsNullOrEmpty(good_quality)) {
            this.good_quality = new ArrayList<String>(Arrays.asList(good_quality.split(",")));
        }
    }

    public List<String> getBad_quality() {
        return bad_quality;
    }

    public void setBad_quality(List<String> bad_quality) {
        this.bad_quality = bad_quality;
    }

    public void setBad_quality(String bad_quality) {
        if (!Utils.IsNullOrEmpty(bad_quality)) {
            this.bad_quality = new ArrayList<String>(Arrays.asList(bad_quality.split(",")));
        }
    }
}
