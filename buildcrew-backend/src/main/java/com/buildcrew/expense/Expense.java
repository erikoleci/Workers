package com.buildcrew.expense;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    @Column(name = "project_id")
    public UUID projectId;

    public String category;

    public BigDecimal amount;

    @Column(name = "expense_date")
    public LocalDate expenseDate;

    public String description;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
