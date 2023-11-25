package com.tariqkhan051.reviewrover.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.helpers.Messages.ResponseMessages;
import com.tariqkhan051.reviewrover.models.Department;
import com.tariqkhan051.reviewrover.models.Team;
import com.tariqkhan051.reviewrover.payload.request.CreateTeamRequest;
import com.tariqkhan051.reviewrover.payload.request.UpdateTeamRequest;
import com.tariqkhan051.reviewrover.payload.response.MessageResponse;
import com.tariqkhan051.reviewrover.repository.DepartmentRepository;
import com.tariqkhan051.reviewrover.repository.TeamRepository;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class TeamServiceController {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @CrossOrigin
    @RequestMapping(value = "/teams", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Object> getTeams() {

        // var dbHandler = new DbHandler();
        // var teams = dbHandler.GetTeams();

        try {
            var teams = teamRepository.findAll();

            if (teams.size() > 0) {
                return ResponseMessages.SuccessResponseData(teams, "Teams retrieved successfully.");
            }

            return ResponseMessages.SuccessResponse("No teams found.");

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to retrieve teams.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/teams", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addTeam(@Valid @RequestBody CreateTeamRequest createTeamRequest) {

        // if (!TeamValidator.IsValidTeam(team)) {
        // return ResponseMessages.MissingFieldsResponse();
        // }

        try {
            if (teamRepository.existsByName(createTeamRequest.getName())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Team name already exists!"));
            }

            if (Utils.IsNullOrEmpty(createTeamRequest.getName())) {
                return ResponseMessages.ErrorResponse("Team name is required.");
            }

            Team team = new Team(createTeamRequest.getName());

            if (createTeamRequest.getDepartment_id() != null && createTeamRequest.getDepartment_id() > 0) {
                if (departmentRepository.existsById(createTeamRequest.getDepartment_id()) == false) {
                    return ResponseMessages.ErrorResponse("Department not found.");
                }

                Department department = departmentRepository.findById(createTeamRequest.getDepartment_id()).get();
                team.setDepartment(department);
            }

            team.setCreatedOn(Utils.GetCurrentTimeStamp());
            teamRepository.save(team);

            return ResponseMessages.SuccessResponse("Team added successfully.");
            // return ResponseEntity.ok(new MessageResponse("Team added successfully!"));

            // DbHandler dbHandler = new DbHandler();
            // if (dbHandler.AddTeam(team)) {
            // return ResponseMessages.SuccessResponse("Team created successfully.");
            // }

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to create team!");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/teams/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateTeam(@PathVariable("id") Long id,
            @RequestBody UpdateTeamRequest updateTeamRequest) {
        // var dbHandler = new DbHandler();
        // var isUpdated = dbHandler.UpdateTeam(team, name);

        // if (isUpdated) {
        // return ResponseMessages.SuccessResponse("Team is updated successfully.");
        // }

        // return ResponseMessages.ErrorResponse("Unable to update team.");

        try {

            var team = teamRepository.findById(id);

            if (team.isPresent()) {
                Team teamToUpdate = team.get();

                if (!teamToUpdate.getName().equals(updateTeamRequest.getName())) {

                    if (teamRepository.existsByName(updateTeamRequest.getName())) {
                        return ResponseMessages.ErrorResponse("Team name already exists.");
                    }

                    teamToUpdate.setName(updateTeamRequest.getName());
                }

                if (updateTeamRequest.getDepartment_id() != null)
                {
                    var department = departmentRepository.findById(updateTeamRequest.getDepartment_id());
                    if (department.isPresent())
                    {
                        teamToUpdate.setDepartment(department.get());
                    }
                }
                
                teamRepository.save(teamToUpdate);

                return ResponseMessages.SuccessResponse("Team updated successfully.");
            }

            return ResponseMessages.ErrorResponse("Team not found.");

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to update team.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/teams/{team}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteTeam(@PathVariable("team") Long id) {

        if (id == null) {
            return ResponseMessages.MissingFieldsResponse();
        }

        // var team = new Team();
        // team.setName(name);

        // var dbHandler = new DbHandler();
        // var isDeleted = dbHandler.DeleteTeam(team);

        // if (isDeleted) {
        // return ResponseMessages.SuccessResponse("Team is deleted successfully.");
        // }

        // return ResponseMessages.ErrorResponse("Unable to delete team.");
        try {
            var team = teamRepository.findById(id);

            if (!team.isPresent()) {
                return ResponseMessages.ErrorResponse("Team not found.");
            }

            teamRepository.delete(team.get());

            return ResponseMessages.SuccessResponse("Team deleted successfully!");
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to delete team!");
        }
    }
}
