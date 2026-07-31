package com.buildcrew.user;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {

    public List<User> findByCompany(UUID companyId, String role) {
        if (role != null && !role.isBlank()) {
            return find("companyId = :companyId and role = :role and status = 'active'",
                    Sort.by("name"),
                    Parameters.with("companyId", companyId).and("role", role))
                    .list();
        }
        return find("companyId = :companyId and status = 'active'",
                Sort.by("name"),
                Parameters.with("companyId", companyId))
                .list();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}
