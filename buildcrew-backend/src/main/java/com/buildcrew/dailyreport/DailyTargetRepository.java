package com.buildcrew.dailyreport;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DailyTargetRepository implements PanacheRepositoryBase<DailyTarget, UUID> {

    public DailyTarget findByProjectAndDate(UUID projectId, LocalDate date) {
        return find("projectId = ?1 and targetDate = ?2", projectId, date).firstResult();
    }

    public List<DailyTarget> findByProject(UUID projectId) {
        return list("projectId", projectId);
    }
}
