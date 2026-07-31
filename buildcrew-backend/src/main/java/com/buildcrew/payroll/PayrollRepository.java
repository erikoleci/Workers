package com.buildcrew.payroll;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PayrollRepository implements PanacheRepositoryBase<Payroll, UUID> {

    public List<Payroll> search(UUID companyId, String status, UUID workerId, int page, int size) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        Parameters params = Parameters.with("companyId", companyId);

        if (status != null && !status.isBlank()) {
            jpql.append(" and status = :status");
            params.and("status", status);
        }
        if (workerId != null) {
            jpql.append(" and workerId = :workerId");
            params.and("workerId", workerId);
        }

        return find(jpql.toString(), Sort.descending("periodEnd"), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countSearch(UUID companyId, String status, UUID workerId) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        Parameters params = Parameters.with("companyId", companyId);

        if (status != null && !status.isBlank()) {
            jpql.append(" and status = :status");
            params.and("status", status);
        }
        if (workerId != null) {
            jpql.append(" and workerId = :workerId");
            params.and("workerId", workerId);
        }

        return find(jpql.toString(), params).count();
    }
}
