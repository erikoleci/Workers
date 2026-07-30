package com.buildcrew.crew;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CrewMemberRepository implements PanacheRepositoryBase<CrewMember, UUID> {

    public List<CrewMember> findByCrew(UUID crewId) {
        return list("crewId", crewId);
    }

    public void removeAllFromCrew(UUID crewId) {
        delete("crewId", crewId);
    }
}
