package com.buildcrew.workerreport;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkerDailyReportRepository implements PanacheRepositoryBase<WorkerDailyReport, UUID> {

    public List<WorkerDailyReport> findByWorker(UUID workerId, int page, int size) {
        return find("workerId", Sort.descending("reportDate"), workerId)
                .page(Page.of(page, size))
                .list();
    }

    public long countByWorker(UUID workerId) {
        return count("workerId", workerId);
    }

    public WorkerDailyReport findByWorkerProjectDate(UUID workerId, UUID projectId, java.time.LocalDate date) {
        return find("workerId = ?1 and projectId = ?2 and reportDate = ?3", workerId, projectId, date)
                .firstResult();
    }
}
