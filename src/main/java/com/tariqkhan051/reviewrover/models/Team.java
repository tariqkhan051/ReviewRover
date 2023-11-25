package com.tariqkhan051.reviewrover.models;

import java.sql.Timestamp;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "teams", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Column(name = "createdOn", columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE", updatable = false)
    @CreationTimestamp
    private Timestamp createdOn;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_teams_department"))
    private Department department;
    
    // @ManyToMany(cascade = CascadeType.ALL)
    // @JoinTable(name = "user_team", 
    // joinColumns = @JoinColumn(name = "team_id", 
    // foreignKey = @ForeignKey(name = "fk_user_team__team"), 
    // nullable = false), 
    // inverseJoinColumns = @JoinColumn(name = "user_id", 
    // foreignKey = @ForeignKey(name = "fk_user_team_user"), nullable = false))
    // private Set<User> users;
    
    // @ManyToMany(cascade = CascadeType.ALL)
    // @JoinTable(name = "users", joinColumns = @JoinColumn(name = "team_id"))
    // private Set<User> users;

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
