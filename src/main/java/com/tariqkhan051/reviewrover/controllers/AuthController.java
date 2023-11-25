package com.tariqkhan051.reviewrover.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tariqkhan051.reviewrover.exception.TokenRefreshException;
import com.tariqkhan051.reviewrover.helpers.Utils;
import com.tariqkhan051.reviewrover.models.ERole;
import com.tariqkhan051.reviewrover.models.RefreshToken;
import com.tariqkhan051.reviewrover.models.Role;
import com.tariqkhan051.reviewrover.models.User;
import com.tariqkhan051.reviewrover.payload.request.LoginRequest;
import com.tariqkhan051.reviewrover.payload.request.SignupRequest;
import com.tariqkhan051.reviewrover.payload.request.TokenRefreshRequest;
import com.tariqkhan051.reviewrover.payload.response.JwtResponse;
import com.tariqkhan051.reviewrover.payload.response.MessageResponse;
import com.tariqkhan051.reviewrover.payload.response.TokenRefreshResponse;
import com.tariqkhan051.reviewrover.repository.RoleRepository;
import com.tariqkhan051.reviewrover.repository.TeamRepository;
import com.tariqkhan051.reviewrover.repository.UserRepository;
import com.tariqkhan051.reviewrover.security.jwt.JwtUtils;
import com.tariqkhan051.reviewrover.security.services.RefreshTokenService;
import com.tariqkhan051.reviewrover.security.services.UserDetailsImpl;
import com.tariqkhan051.reviewrover.repository.JobRepository;
import com.tariqkhan051.reviewrover.helpers.Messages.ResponseMessages;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TeamRepository teamRepository;

  @Autowired
  JobRepository jobRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);

    List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        .collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    User user = userRepository.findById(userDetails.getId()).get();

    if (!user.getIsLive()) {
      return ResponseEntity.badRequest().body(new MessageResponse(ResponseMessages.USER_IS_NOT_LIVE));
    }

    user.setLastLogin(Utils.GetCurrentTimeStamp());
    userRepository.save(user);

    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
        userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (Utils.IsNullOrEmpty(signUpRequest.getName())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Name is required!"));
    }

    if (Utils.IsNullOrEmpty(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is required!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    if (!Utils.IsNullOrEmpty(signUpRequest.getManager_name()) &&
        !userRepository.existsByUsername(signUpRequest.getManager_name())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Manager not found!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), signUpRequest.getName(), signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
            break;
          case "user":
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);

    if (!Utils.IsNullOrEmpty(signUpRequest.getManager_name())) {
      User manager = userRepository.findByUsername(signUpRequest.getManager_name()).get();
      user.setManager(manager);
      manager.getSubordinates().add(user);
    }

    var teamName = signUpRequest.getTeam_name();

    if (teamName != null) {

      if (!teamRepository.existsByName(teamName)) {
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Team not found!"));
      }

      var team = teamRepository.findByName(teamName).get();
      user.setTeam(team);

    }

    if (!Utils.IsNullOrEmpty(signUpRequest.getJob_name())) {

      if (!jobRepository.existsByName(signUpRequest.getJob_name())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Job not found!"));
      }

      var job = jobRepository.findByName(signUpRequest.getJob_name());
      user.setJob(job.get());
    }

    user.setIsLive(signUpRequest.getIs_live());

    user.setCreatedOn(Utils.GetCurrentTimeStamp());

    userRepository.save(user);

    return ResponseMessages.SuccessResponse("User registered successfully.");
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
            "Refresh token is not in database!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof AnonymousAuthenticationToken)
      return ResponseEntity.badRequest().body(new MessageResponse("No user logged in!"));

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    Long userId = userDetails.getId();
    refreshTokenService.deleteByUserId(userId);
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }
}
