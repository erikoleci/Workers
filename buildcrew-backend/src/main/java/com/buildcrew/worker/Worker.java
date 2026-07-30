package com.buildcrew.worker;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workers")
public class Worker extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    @Column(name = "full_name")
    public String fullName;

    public String phone;

    public String position;

    @Column(name = "pay_type")
    public String payType; // daily | per_m2

    @Column(name = "daily_salary")
    public BigDecimal dailySalary;

    @Column(name = "price_per_m2")
    public BigDecimal pricePerM2;

    @Column(name = "employment_date")
    public LocalDate employmentDate;

    public String status;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;

    public static io.quarkus.panache.common.Page pageOf(int page, int size) {
        return io.quarkus.panache.common.Page.of(page, size);
    }
}
