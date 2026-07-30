package com.buildcrew.auth;

public class LoginResponse {

    public String token;
    public String name;
    public String email;
    public String role;
    public String companyId;

    public LoginResponse(String token, String name, String email, String role, String companyId) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.role = role;
        this.companyId = companyId;
    }
}
