package com.tariqkhan051.reviewrover.payload.request;

import javax.validation.constraints.*;

public class CreateTeamRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    private Long department_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }
}
