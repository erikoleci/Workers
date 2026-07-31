package com.buildcrew.client;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class Client extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "company_id")
    public UUID companyId;

    @Column(name = "company_name")
    public String companyName;

    @Column(name = "contact_person")
    public String contactPerson;

    public String phone;

    public String email;

    public String address;

    @Column(name = "created_at")
    public OffsetDateTime createdAt;
}
