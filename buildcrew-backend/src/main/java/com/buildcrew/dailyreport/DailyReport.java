package com.buildcrew.dailyreport;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_reports")
public class DailyReport extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "project_id")
    public UUID projectId;

    @Column(name = "crew_id")
    public UUID crewId;

    @Column(name = "report_date")
    public LocalDate reportDate;

    @Column(name = "completed_m2")
    public BigDecimal completedM2;

    @Column(name = "worked_hours")
    public BigDecimal workedHours;

    public String comments;

    @Column(name = "created_by")
    public UUID createdBy;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
