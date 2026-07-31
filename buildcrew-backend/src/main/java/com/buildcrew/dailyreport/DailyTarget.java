package com.buildcrew.dailyreport;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "daily_targets")
public class DailyTarget extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "project_id")
    public UUID projectId;

    @Column(name = "target_date")
    public LocalDate targetDate;

    @Column(name = "target_m2")
    public BigDecimal targetM2;
}
