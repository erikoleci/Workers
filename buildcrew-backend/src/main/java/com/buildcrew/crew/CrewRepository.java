package com.buildcrew.crew;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CrewRepository implements PanacheRepositoryBase<Crew, UUID> {

    public List<Crew> search(UUID companyId, String query, int page, int size) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        if (query != null && !query.isBlank()) jpql.append(" and lower(name) like :query");

        Parameters params = Parameters.with("companyId", companyId);
        if (query != null && !query.isBlank()) params.and("query", "%" + query.toLowerCase() + "%");

        return find(jpql.toString(), Sort.by("name"), params)
                .page(Page.of(page, size))
                .list();
    }

    public long countSearch(UUID companyId, String query) {
        StringBuilder jpql = new StringBuilder("companyId = :companyId");
        if (query != null && !query.isBlank()) jpql.append(" and lower(name) like :query");

        Parameters params = Parameters.with("companyId", companyId);
        if (query != null && !query.isBlank()) params.and("query", "%" + query.toLowerCase() + "%");

        return find(jpql.toString(), params).count();
    }
}
