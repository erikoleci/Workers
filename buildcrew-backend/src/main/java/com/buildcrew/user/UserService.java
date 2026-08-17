package com.buildcrew.user;

import com.buildcrew.security.TenantContext;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    TenantContext tenantContext;

    @Transactional
    public List<UserDTO> list(String role) {
        UUID companyId = tenantContext.getCompanyId();
        return userRepository.findByCompany(companyId, role).stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.email)) {
            // Clean 409 instead of letting the DB's unique(email) constraint
            // surface as a raw 500 at transaction-flush time.
            throw new WebApplicationException("A user with this email already exists", Response.Status.CONFLICT);
        }

        User user = new User();
        user.id = UUID.randomUUID();
        user.companyId = tenantContext.getCompanyId();
        user.name = dto.name;
        user.email = dto.email;
        user.passwordHash = BcryptUtil.bcryptHash(dto.password);
        user.role = dto.role;
        user.phone = dto.phone;
        user.status = "active";
        user.createdAt = OffsetDateTime.now();

        userRepository.persist(user);
        return toDTO(user);
    }

    @Transactional
    public void deactivate(UUID id) {
        User user = userRepository.findById(id);
        if (user == null || !user.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("User not found");
        }
        user.status = "inactive";
    }

    @Transactional
    public void resetPassword(UUID id, String newPassword) {
        User user = userRepository.findById(id);
        if (user == null || !user.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("User not found");
        }
        user.passwordHash = BcryptUtil.bcryptHash(newPassword);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.id.toString();
        dto.name = user.name;
        dto.email = user.email;
        dto.role = user.role;
        dto.phone = user.phone;
        dto.status = user.status;
        return dto;
    }
}
