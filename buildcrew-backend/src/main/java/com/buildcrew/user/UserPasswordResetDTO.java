package com.buildcrew.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserPasswordResetDTO {

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    public String newPassword;
}
