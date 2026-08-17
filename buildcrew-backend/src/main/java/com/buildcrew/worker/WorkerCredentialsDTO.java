package com.buildcrew.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WorkerCredentialsDTO {

    @NotBlank
    public String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    public String password;
}
