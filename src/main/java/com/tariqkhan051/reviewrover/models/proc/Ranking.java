package com.tariqkhan051.reviewrover.models.proc;

public class Ranking {
    private String name;
    private String team_name;
    private int year;
    private int month;
    private double score;
    private String good_quality;
    private String bad_quality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getGood_quality() {
        return good_quality;
    }

    public void setGood_quality(String good_quality) {
        this.good_quality = good_quality;
    }

    public String getBad_quality() {
        return bad_quality;
    }

    public void setBad_quality(String bad_quality) {
        this.bad_quality = bad_quality;
    }
}
