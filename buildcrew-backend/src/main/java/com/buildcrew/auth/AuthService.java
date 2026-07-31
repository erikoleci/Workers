package com.buildcrew.auth;

import com.buildcrew.security.JwtService;
import com.buildcrew.user.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;

@ApplicationScoped
public class AuthService {

    @Inject
    JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = User.findByIdentifier(request.identifier);

        if (user == null || !"active".equals(user.status)) {
            throw new NotAuthorizedException("Invalid credentials");
        }

        if (!BcryptUtil.matches(request.password, user.passwordHash)) {
            throw new NotAuthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.name,
                user.email,
                user.role,
                user.companyId.toString()
        );
    }
}
