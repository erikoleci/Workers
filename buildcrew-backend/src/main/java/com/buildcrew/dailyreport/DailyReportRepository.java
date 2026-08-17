package com.buildcrew.dailyreport;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DailyReportRepository implements PanacheRepositoryBase<DailyReport, UUID> {

    public List<DailyReport> search(UUID companyId, UUID projectId, UUID crewId, LocalDate from, LocalDate to, int page, int size) {
        StringBuilder jpql = new StringBuilder(
                "projectId in (select p.id from Project p where p.companyId = :companyId)");
        Parameters params = Parameters.with("companyId", companyId);

        if (projectId != null) { jpql.append(" and projectId = :projectId"); params.and("projectId", projectId); }
        if (crewId != null) { jpql.append(" and crewId = :crewId"); params.and("crewId", crewId); }
        if (from != null) { jpql.append(" and reportDate >= :from"); params.and("from", from); }
        if (to != null) { jpql.append(" and reportDate <= :to"); params.and("to", to); }

        return find(jpql.toString(), Sort.descending("reportDate"), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countSearch(UUID companyId, UUID projectId, UUID crewId, LocalDate from, LocalDate to) {
        StringBuilder jpql = new StringBuilder(
                "projectId in (select p.id from Project p where p.companyId = :companyId)");
        Parameters params = Parameters.with("companyId", companyId);

        if (projectId != null) { jpql.append(" and projectId = :projectId"); params.and("projectId", projectId); }
        if (crewId != null) { jpql.append(" and crewId = :crewId"); params.and("crewId", crewId); }
        if (from != null) { jpql.append(" and reportDate >= :from"); params.and("from", from); }
        if (to != null) { jpql.append(" and reportDate <= :to"); params.and("to", to); }

        return find(jpql.toString(), params).count();
    }

    public DailyReport findByProjectCrewDate(UUID projectId, UUID crewId, LocalDate date) {
        return find("projectId = ?1 and crewId = ?2 and reportDate = ?3", projectId, crewId, date)
                .firstResult();
    }
}
