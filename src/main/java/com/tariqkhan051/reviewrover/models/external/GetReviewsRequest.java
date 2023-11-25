package com.tariqkhan051.reviewrover.models.external;

import java.util.Optional;

import com.tariqkhan051.reviewrover.models.EReviewStatus;
import com.tariqkhan051.reviewrover.models.EReviewType;

public class GetReviewsRequest {
    private Integer month;
    private Integer year;
    private String type;
    private String team;
    private String user;
    private String status;
    private String department;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(Optional<String> department) {
        if (department.isPresent()) {
            this.department = department.get();
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(Optional<String> status) {
        if (status.isPresent()) {
            switch (status.get()) {
                case "pending":
                case "PENDING":
                    this.status = EReviewStatus.PENDING.toString();
                case "approved":
                case "APPROVED":
                default:
                    this.status = EReviewStatus.APPROVED.toString();
            }
        } else {
            this.status = EReviewStatus.APPROVED.toString();
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(Optional<String> user) {
        if (user.isPresent()) {
            this.user = user.get();
        }
    }

    public Integer getMonth() {
        return month != null ? month : 0;
    }

    public void setMonth(Optional<Integer> month) {
        if (month.isPresent()) {
            this.month = month.get();
        }
    }

    public Integer getYear() {
        return year != null ? year : 0;
    }

    public void setYear(Optional<Integer> year) {
        if (year.isPresent()) {
            this.year = year.get();
        }
    }

    public String getType() {
        if (type == null) {
            return "";
        }
        return type;
    }

    public void setType(Optional<String> type) {
        if (type.isPresent()) {
            switch (type.get()) {
                case "random":
                case "RANDOM":
                    this.type = EReviewType.RANDOM.toString();
                    break;
                case "monthly":
                case "MONTHLY":
                    this.type = EReviewType.MONTHLY.toString();
                    break;
                default:
                    this.type = "";
                    break;
            }
        } else {
            this.type = "";
        }
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(Optional<String> team) {
        if (team.isPresent()) {
            this.team = team.get();
        }
    }

}
