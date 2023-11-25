package com.tariqkhan051.reviewrover.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tariqkhan051.reviewrover.DbHandler;
import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.helpers.Messages.ResponseMessages;
import com.tariqkhan051.reviewrover.helpers.Validator.UserValidator;
import com.tariqkhan051.reviewrover.models.ERole;
import com.tariqkhan051.reviewrover.models.Team;
import com.tariqkhan051.reviewrover.models.User;
import com.tariqkhan051.reviewrover.payload.request.UpdateUserRequest;
import com.tariqkhan051.reviewrover.payload.response.UserDetailsResponse;
import com.tariqkhan051.reviewrover.payload.response.MessageResponse;
import com.tariqkhan051.reviewrover.repository.JobRepository;
import com.tariqkhan051.reviewrover.repository.ReviewRepository;
import com.tariqkhan051.reviewrover.repository.RoleRepository;
import com.tariqkhan051.reviewrover.repository.TeamRepository;
import com.tariqkhan051.reviewrover.repository.UserRepository;
import com.tariqkhan051.reviewrover.security.services.RefreshTokenService;
import com.tariqkhan051.reviewrover.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@CrossOrigin
@RestController
@RequestMapping("/api/test")
public class UserServiceController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PasswordEncoder encoder;

    @CrossOrigin
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getUsers(@RequestParam Optional<String> team) {
        try {
            List<User> users;

            if (team.isPresent()) {
                users = userRepository.findByTeam(teamRepository.findByName(team.get()).get());
            } else {
                users = userRepository.findAll();
            }

            // var dbHandler = new DbHandler();
            // var response = dbHandler.GetUsers(team_name);

            var usersCount = users.size();

            if (usersCount > 0) {

                var adminRole = roleRepository.findByName(ERole.ROLE_ADMIN);

                if (adminRole.isPresent()) {

                    List<User> nonAdminUsers = users.stream()
                            .filter(u -> !u.getRoles().contains(
                                    adminRole
                                            .get()))
                            .toList();

                    return ResponseMessages.SuccessResponseData(
                            nonAdminUsers,
                            "Users retrieved successfully.");
                }

                return ResponseMessages.SuccessResponseData(
                        users,
                        "Users retrieved successfully.");
            }

            return ResponseMessages.SuccessResponse("No users found.");
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to retrieve users.");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/users/{team}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> getUsersForTeam(@PathVariable("team") String team) {
        try {
            List<User> users;

            if (Utils.IsNullOrEmpty(team)) {
                return ResponseMessages.ErrorResponse("Provide team name!");
            }

            var selectedTeam = teamRepository.findByName(team)
                    .orElseThrow(() -> new RuntimeException("Error: Team is not found."));

            users = userRepository.findByTeam(selectedTeam);

            var usersCount = users.size();

            if (usersCount > 0) {

                List<User> activeUsers = users.stream()
                        .filter(u -> u.getIsLive().equals(true))
                        .toList();

                return ResponseMessages.SuccessResponseData(
                        activeUsers,
                        "Users retrieved successfully.");
            } else {
                return ResponseMessages.ErrorResponse("No users found.");
            }
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse(e.getMessage());
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/userdetails", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> getUserDetails() {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof AnonymousAuthenticationToken)
                return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String username = userDetails.getUsername();

            if (!userRepository.existsByUsername(username)) {
                return ResponseMessages.ErrorResponse("User not found.");
            }

            User user = userRepository.findByUsername(username).get();

            UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
            userDetailsResponse.setName(user.getName());

            Team team = user.getTeam();
            if (team == null) {
                userDetailsResponse.setTeam_name("");
            } else {
                userDetailsResponse.setTeam_name(team.getName());
            }
            userDetailsResponse.setIs_manager(userRepository.existsByManagerId(user.getId()));

            return ResponseMessages.SuccessResponseData(userDetailsResponse, "User details retrieved successfully.");

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse(e.getMessage());
        }
    }

    // @CrossOrigin
    // @RequestMapping(value = "/users/validate", method = RequestMethod.POST)
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    // public ResponseEntity<Object> verifyUser(@RequestBody User user) {

    // if (!UserValidator.IsAuthenticatedUser(user)) {
    // return ResponseMessages.MissingFieldsResponse();
    // }

    // DbHandler dbHandler = new DbHandler();

    // if (dbHandler.GetUserByNameAndPassword(user)) {
    // return ResponseMessages.SuccessResponse("User verified successfully.");
    // }

    // return ResponseMessages.UnAuthorizedResponse("Unable to verify user.");
    // }

    // @CrossOrigin
    // @RequestMapping(value = "/users", method = RequestMethod.POST)
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<Object> createUser(@RequestBody User user) {
    // try {

    // if (!UserValidator.IsValidUser(user)) {
    // return ResponseMessages.MissingFieldsResponse();
    // }

    // DbHandler dbHandler = new DbHandler();
    // var isAdded = dbHandler.AddUser(user);

    // if (isAdded) {
    // return ResponseMessages.SuccessResponse("User is added successfully.");
    // }

    // return ResponseMessages.SuccessResponse("User is added successfully.");
    // } catch (Exception e) {
    // return ResponseMessages.ExceptionResponse("Unable to add user.");
    // }
    // }

    @CrossOrigin
    @RequestMapping(value = "/users/{username}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> updateUser(@PathVariable("username") String username,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {

        try {

            if (!userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username not found!"));
            }

            User user = userRepository.findByUsername(username).get();

            if (!Utils.IsNullOrEmpty(updateUserRequest.getUsername()) &&
                    !username.equals(updateUserRequest.getUsername())) {

                if (userRepository.existsByUsername(updateUserRequest.getUsername())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Username already exists!"));
                }

                user.setUsername(updateUserRequest.getUsername());
            }

            if (!Utils.IsNullOrEmpty(updateUserRequest.getEmail()) &&
                    !user.getEmail().equals(updateUserRequest.getEmail())) {

                if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Email already exists!"));
                }

                user.setEmail(updateUserRequest.getEmail());
            }

            if (!Utils.IsNullOrEmpty(updateUserRequest.getName())) {
                user.setName(updateUserRequest.getName());
            }

            if (!Utils.IsNullOrEmpty(updateUserRequest.getPassword())) {
                user.setPassword(encoder.encode(updateUserRequest.getPassword()));
            }

            var teamName = updateUserRequest.getTeam_name();

            if (!Utils.IsNullOrEmpty(teamName) && !teamRepository.existsByName(teamName)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Team not found!"));
            } else {
                Team team = teamRepository.findByName(teamName).get();
                user.setTeam(team);
            }

            var managerName = updateUserRequest.getManager_name();

            if (!Utils.IsNullOrEmpty(managerName) && !managerName.equals(username)) {

                if (!userRepository.existsByUsername(managerName)) {
                    return ResponseEntity.badRequest().body(
                            new MessageResponse("Error: No user found with this username to be selected as manager!"));

                }

                User manager = userRepository.findByUsername(managerName).get();

                if (manager.getManager() != null && !Utils.IsNullOrEmpty(manager.getManager().getUsername()) &&
                        manager.getManager().getUsername().equals(username)) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: User can't be manager!"));
                }

                user.setManager(manager);
            } else if (Utils.IsNullOrEmpty(managerName)) {
                user.setManager(null);
            }

            if (updateUserRequest.getIs_live() != null) {
                user.setIsLive(updateUserRequest.getIs_live());
            }

            if (!Utils.IsNullOrEmpty(updateUserRequest.getJob_name())) {

                if (!jobRepository.existsByName(updateUserRequest.getJob_name())) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Job not found!"));
                }

                var job = jobRepository.findByName(updateUserRequest.getJob_name());
                user.setJob(job.get());
            }

            userRepository.save(user);

            return ResponseMessages.SuccessResponse("User is updated successfully.");

            // if (user != null) {
            // user.setUsername(username);
            // }

            // if (!UserValidator.IsValidRequest(user)) {
            // return ResponseMessages.MissingFieldsResponse();
            // }

            // DbHandler dbHandler = new DbHandler();
            // var isUpdated = dbHandler.UpdateUser(user);

            // if (isUpdated) {
            // return ResponseMessages.SuccessResponse("User is updated successfully.");
            // }

        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to update user!");
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/users/{username}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> delete(@PathVariable("username") String username,
            @RequestParam Optional<Boolean> force) {

        // User user = new User();
        // user.setUsername(username);

        // var dbHandler = new DbHandler();
        // var isDeleted = dbHandler.DeleteUser(user);

        // if (isDeleted) {
        // return ResponseMessages.SuccessResponse("User is deleted successfully.");
        // }

        try {
            if (!userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username not found!"));
            }

            User user = userRepository.findByUsername(username).get();

            if (force.isPresent() && force.get() == true) {
                refreshTokenService.deleteByUserId(user.getId());
                if (reviewRepository.existsBySubmittedByOrReviewFor(user, user)) {

                    // reviewRepository.getReviewsBySubmittedBy(user).forEach(review -> {
                    // reviewRepository.delete(review);
                    // });

                    // var deletedCount =
                    // reviewRepository.deleteReviewsBySubmittedByOrReviewFor(user.getId(),
                    // user.getId());

                    reviewRepository.getReviewsByReviewFor(user).forEach(review -> {
                        reviewRepository.delete(review);
                    });

                    // int reviewsDeleted = reviewRepository.deleteBySubmittedBy(user);
                }
                // int rolesDeleted = roleRepository.deleteByUser(user);
            }

            user.setRoles(null);
            user.setTeam(null);
            user.setManager(null);
            user.setJob(null);

            userRepository.save(user);

            userRepository.delete(user);

            return ResponseMessages.SuccessResponse("User is deleted successfully.");
        } catch (Exception e) {
            return ResponseMessages.ExceptionResponse("Unable to delete user.");
        }
    }
}
