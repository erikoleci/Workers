package com.buildcrew.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@RequestScoped
public class TenantContext {

    @Inject
    JsonWebToken jwt;

    public UUID getCompanyId() {
        String companyId = jwt.getClaim("company_id");
        return UUID.fromString(companyId);
    }

    public String getRole() {
        return jwt.getClaim("role");
    }

    public UUID getUserId() {
        return UUID.fromString(jwt.getSubject());
    }
}
