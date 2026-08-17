package com.buildcrew.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import com.buildcrew.user.User;
import com.buildcrew.worker.Worker;

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

    /** Workers log in separately from office staff (users table) - always get role "worker". */
    public String generateToken(Worker worker) {
        return Jwt.issuer("buildcrew-manager")
                .subject(worker.id.toString())
                .claim("company_id", worker.companyId.toString())
                .claim("name", worker.fullName)
                .claim("role", "worker")
                .groups("worker")
                .expiresIn(Duration.ofHours(12))
                .sign();
    }
}
