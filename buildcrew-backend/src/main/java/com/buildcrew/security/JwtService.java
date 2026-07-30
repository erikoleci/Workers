package com.buildcrew.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import com.buildcrew.user.User;

import java.time.Duration;

@ApplicationScoped
public class JwtService {

    public String generateToken(User user) {
        return Jwt.issuer("buildcrew-manager")
                .subject(user.id.toString())
                .claim("company_id", user.companyId.toString())
                .claim("email", user.email)
                .claim("role", user.role)
                .groups(user.role) // enables @RolesAllowed
                .expiresIn(Duration.ofHours(12))
                .sign();
    }
}
