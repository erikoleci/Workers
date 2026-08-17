package com.buildcrew.workerreport;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "worker_daily_reports")
public class WorkerDailyReport extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "worker_id")
    public UUID workerId;

    @Column(name = "project_id")
    public UUID projectId;

    @Column(name = "report_date")
    public LocalDate reportDate;

    @Column(name = "completed_m2")
    public BigDecimal completedM2;

    public String comments;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
