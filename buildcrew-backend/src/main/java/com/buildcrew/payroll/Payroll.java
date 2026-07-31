package com.buildcrew.payroll;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payroll")
public class Payroll extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    @Column(name = "worker_id")
    public UUID workerId;

    @Column(name = "period_start")
    public LocalDate periodStart;

    @Column(name = "period_end")
    public LocalDate periodEnd;

    @Column(name = "base_amount")
    public BigDecimal baseAmount;

    public BigDecimal bonuses;

    public BigDecimal deductions;

    @Column(name = "final_amount")
    public BigDecimal finalAmount;

    public String status; // pending | paid

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
