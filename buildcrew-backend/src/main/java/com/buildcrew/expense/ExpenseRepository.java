package com.buildcrew.expense;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExpenseRepository implements PanacheRepositoryBase<Expense, UUID> {

    public List<Expense> search(UUID companyId, UUID projectId, int page, int size) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        Parameters params = Parameters.with("companyId", companyId);
        if (projectId != null) {
            jpql.append(" and projectId = :projectId");
            params.and("projectId", projectId);
        }
        return find(jpql.toString(), Sort.descending("expenseDate"), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countSearch(UUID companyId, UUID projectId) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        Parameters params = Parameters.with("companyId", companyId);
        if (projectId != null) {
            jpql.append(" and projectId = :projectId");
            params.and("projectId", projectId);
        }
        return find(jpql.toString(), params).count();
    }
}
