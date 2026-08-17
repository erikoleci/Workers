package com.buildcrew.notification;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    @Column(name = "user_id")
    public UUID userId;

    public String type; // missing_report | deadline_close | payroll_ready

    public String message;

    @Column(name = "is_read")
    public boolean isRead;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
