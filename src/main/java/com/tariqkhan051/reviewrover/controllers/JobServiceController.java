package com.tariqkhan051.reviewrover.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.helpers.Messages.ResponseMessages;
import com.tariqkhan051.reviewrover.models.Job;
import com.tariqkhan051.reviewrover.payload.request.CreateJobRequest;
import com.tariqkhan051.reviewrover.payload.response.MessageResponse;
import com.tariqkhan051.reviewrover.repository.JobRepository;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class JobServiceController {

    @Autowired
    JobRepository jobRepository;

    @CrossOrigin
    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getJobs() {

        // DbHandler dbHandler = new DbHandler();
        // return ResponseMessages.SuccessResponseData(dbHandler.GetJobs(),
        // "Jobs retrieved successfully.");

        try {
            var jobs = jobRepository.findAll();

            if (jobs.size() > 0) {
                return ResponseMessages.SuccessResponseData(jobs, "Jobs retrieved successfully.");
            }

            return ResponseMessages.ErrorResponse("No jobs found.");

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to retrieve jobs.");
        }
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addJob(@Valid @RequestBody CreateJobRequest createJobRequest) {
        // DbHandler dbHandler = new DbHandler();
        // var isAdded = dbHandler.AddJob(job);

        // if (isAdded) {
        // return ResponseMessages.SuccessResponse("Job added successfully.");
        // } else {
        // return ResponseMessages.ExceptionResponse("Unable to add job.");
        // }

        try {
            if (jobRepository.existsByName(createJobRequest.getName())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Team name already exists!"));
            }

            if (Utils.IsNullOrEmpty(createJobRequest.getName()))
            {
                return ResponseMessages.ErrorResponse("Job name is required.");
            }

            Job job = new Job(createJobRequest.getName());
            job.setCreatedOn(Utils.GetCurrentTimeStamp());
            jobRepository.save(job);

            return ResponseMessages.SuccessResponse("Job added successfully.");
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to add job.");
        }
    }

    @RequestMapping(value = "/jobs/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateJob(@PathVariable("id") Integer id, @RequestBody Job updateJobRequest) {

        // job.setId(id);

        // DbHandler dbHandler = new DbHandler();
        // var isUpdated = dbHandler.UpdateJob(job);

        // if (isUpdated) {
        //     return ResponseMessages.SuccessResponse("Job updated successfully.");
        // } else {
        //     return ResponseMessages.ExceptionResponse("Unable to update job.");
        // }

        try {
            if (!jobRepository.existsByName(updateJobRequest.getName())) {
                
            }
            var jobToUpdate = jobRepository.findById(id);

            if (jobToUpdate.isPresent()) {
                Job job = jobToUpdate.get();
                job.setName(updateJobRequest.getName());
                jobRepository.save(jobToUpdate.get());
                return ResponseMessages.SuccessResponse("Job updated successfully.");
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Job not found!"));
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to update job.");
        }
    }

    @RequestMapping(value = "/jobs/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteJob(@PathVariable("id") Integer id) {
        // Job job = new Job();
        // job.setId(id);

        // DbHandler dbHandler = new DbHandler();
        // var isDeleted = dbHandler.DeleteJob(job);
        // if (isDeleted) {
        //     return ResponseMessages.SuccessResponse("Job deleted successfully.");
        // } else {
        //     return ResponseMessages.ExceptionResponse("Unable to delete job.");
        // }

        try{
            var jobToDelete = jobRepository.findById(id);

            if (jobToDelete.isPresent()) {
                jobRepository.delete(jobToDelete.get());
                return ResponseMessages.SuccessResponse("Job deleted successfully.");
            }
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Job not found!"));
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to delete job.");
        }
    }
}
