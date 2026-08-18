package com.buildcrew.workerreport;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "worker_daily_targets")
public class WorkerDailyTarget extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "worker_id")
    public UUID workerId;

    @Column(name = "target_date")
    public LocalDate targetDate;

    @Column(name = "target_m2")
    public BigDecimal targetM2;

    @Column(name = "set_by")
    public UUID setBy;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;

    public static WorkerDailyTarget find(UUID workerId, LocalDate date) {
        return find("workerId = ?1 and targetDate = ?2", workerId, date).firstResult();
    }
}
