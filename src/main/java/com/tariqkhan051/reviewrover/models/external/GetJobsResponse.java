package com.tariqkhan051.reviewrover.models.external;

import java.util.List;

import com.tariqkhan051.reviewrover.models.Job;

public class GetJobsResponse { 
    public List<Job> jobs;

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}