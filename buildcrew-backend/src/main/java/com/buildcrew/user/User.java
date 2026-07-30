package com.buildcrew.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    public String name;

    public String email;

    @Column(name = "password_hash")
    public String passwordHash;

    public String role; // owner, manager, crew_leader

    public String phone;

    public String status;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;

    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
