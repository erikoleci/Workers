package com.buildcrew.crew;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "crews")
public class Crew extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    public String name;

    @Column(name = "leader_id")
    public UUID leaderId;

    @Column(name = "current_project_id")
    public UUID currentProjectId;

    public String status;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
