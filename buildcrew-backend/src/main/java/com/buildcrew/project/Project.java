package com.buildcrew.project;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    @Column(name = "client_id")
    public UUID clientId;

    public String name;

    public String address;

    @Column(name = "start_date")
    public LocalDate startDate;

    public LocalDate deadline;

    @Column(name = "contract_value")
    public BigDecimal contractValue;

    @Column(name = "total_m2")
    public BigDecimal totalM2;

    @Column(name = "assigned_crew_id")
    public UUID assignedCrewId;

    public String status; // active, delayed, completed, cancelled

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
