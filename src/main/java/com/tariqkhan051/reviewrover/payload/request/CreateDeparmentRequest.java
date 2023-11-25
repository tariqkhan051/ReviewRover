package com.tariqkhan051.reviewrover.payload.request;

import javax.validation.constraints.*;

public class CreateDeparmentRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
