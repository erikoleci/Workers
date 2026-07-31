package com.buildcrew.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateDTO {

    @NotBlank
    public String name;

    @NotBlank
    @Email
    public String email;

    @NotBlank
    @Size(min = 6, message = "password must be at least 6 characters")
    public String password;

    @NotBlank
    @Pattern(regexp = "manager|crew_leader", message = "role must be 'manager' or 'crew_leader'")
    public String role;

    public String phone;
}
