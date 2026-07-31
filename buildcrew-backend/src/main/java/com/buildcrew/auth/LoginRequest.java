package com.buildcrew.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    /** Accepts either the user's email OR their username. */
    @NotBlank
    public String identifier;

    @NotBlank
    public String password;
}
