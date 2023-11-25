package com.tariqkhan051.reviewrover.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String nickName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", 
    joinColumns = @JoinColumn(name = "user_id"), 
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    // @ManyToMany(cascade = CascadeType.ALL)
    // @JoinTable(name = "user_team", joinColumns = @JoinColumn(name = "user_id",
    // foreignKey = @ForeignKey(name = "fk_user_team__user"), nullable = false),
    // inverseJoinColumns = @JoinColumn(name = "team_id", foreignKey =
    // @ForeignKey(name = "fk_user_team_team"), nullable = false))
    // private Set<Team> teams;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = true,
    foreignKey = @ForeignKey(name = "fk_users_teams"))
    private Team team;

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id", nullable = true, 
    foreignKey = @ForeignKey(name = "fk_users_managers"))
    private User manager;

    @OneToMany(mappedBy = "manager")
    @JsonIgnore
    private List<User> subordinates = new ArrayList<User>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "id", nullable = true,
    foreignKey = @ForeignKey(name = "fk_users_jobs"))
    private Job job;

    @NotBlank
    @Column(columnDefinition = "boolean default true")
    private Boolean isLive;

    @NotBlank
    @Column(name = "createdOn", columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE", updatable = false)
    private Timestamp createdOn;
    
    @NotBlank
    @Column(name = "lastLogin", columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE", updatable = true)
    private Timestamp lastLogin;

    public User() {
    }

    public User(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public List<User> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<User> subordinates) {
        this.subordinates = subordinates;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
}
