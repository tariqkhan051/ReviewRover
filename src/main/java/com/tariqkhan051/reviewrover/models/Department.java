package com.tariqkhan051.reviewrover.models;

import java.sql.Timestamp;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    @Column(name = "createdOn", columnDefinition = "TIMESTAMP DEFAULT CURRENT_DATE", updatable = false)
    @CreationTimestamp
    private Timestamp createdOn;

    public Department() {
    }

    public Department(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
