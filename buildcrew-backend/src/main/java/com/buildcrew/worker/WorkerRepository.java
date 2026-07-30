package com.buildcrew.worker;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkerRepository implements PanacheRepositoryBase<Worker, UUID> {

    public List<Worker> search(UUID companyId, String query, String status, int page, int size) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        if (status != null && !status.isBlank()) {
            jpql.append(" and status = :status");
        }
        if (query != null && !query.isBlank()) {
            jpql.append(" and lower(fullName) like :query");
        }

        var params = io.quarkus.panache.common.Parameters.with("companyId", companyId);
        if (status != null && !status.isBlank()) params.and("status", status);
        if (query != null && !query.isBlank()) params.and("query", "%" + query.toLowerCase() + "%");

        return find(jpql.toString(), Sort.by("fullName"), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countSearch(UUID companyId, String query, String status) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        if (status != null && !status.isBlank()) jpql.append(" and status = :status");
        if (query != null && !query.isBlank()) jpql.append(" and lower(fullName) like :query");

        var params = io.quarkus.panache.common.Parameters.with("companyId", companyId);
        if (status != null && !status.isBlank()) params.and("status", status);
        if (query != null && !query.isBlank()) params.and("query", "%" + query.toLowerCase() + "%");

        return find(jpql.toString(), params).count();
    }
}
