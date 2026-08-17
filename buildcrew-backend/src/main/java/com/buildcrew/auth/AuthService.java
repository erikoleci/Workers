package com.buildcrew.auth;

import com.buildcrew.security.JwtService;
import com.buildcrew.user.User;
import com.buildcrew.worker.Worker;
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

        if (user != null) {
            if (!"active".equals(user.status) || !BcryptUtil.matches(request.password, user.passwordHash)) {
                throw new NotAuthorizedException("Invalid credentials");
            }
            String token = jwtService.generateToken(user);
            return new LoginResponse(token, user.name, user.email, user.role, user.companyId.toString());
        }

        // Not office staff - try worker login (username + password set by owner/manager).
        Worker worker = Worker.findByUsername(request.identifier);
        if (worker == null || !"active".equals(worker.status) || worker.passwordHash == null
                || !BcryptUtil.matches(request.password, worker.passwordHash)) {
            throw new NotAuthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(worker);
        return new LoginResponse(token, worker.fullName, null, "worker", worker.companyId.toString());
    }
}
