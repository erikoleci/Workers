package com.buildcrew.company;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company extends PanacheEntityBase {

    @Id
    public UUID id;

    public String name;

    @Column(name = "subscription_plan")
    public String subscriptionPlan;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
