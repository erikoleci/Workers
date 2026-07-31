package com.buildcrew.project;

import jakarta.validation.constraints.NotBlank;

public class ProjectStatusUpdateDTO {
    @NotBlank
    public String status; // active, delayed, completed, cancelled
}
