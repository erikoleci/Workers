package com.buildcrew.crew;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "crew_members")
public class CrewMember extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "crew_id")
    public UUID crewId;

    @Column(name = "worker_id")
    public UUID workerId;

    @Column(name = "joined_at")
    public OffsetDateTime joinedAt;
}
