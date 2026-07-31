package com.buildcrew.expense;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ExpenseService {

    @Inject
    ExpenseRepository expenseRepository;

    @Inject
    TenantContext tenantContext;

    public PageResponse<ExpenseDTO> search(String projectId, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();
        UUID pId = projectId != null ? UUID.fromString(projectId) : null;

        List<ExpenseDTO> items = expenseRepository.search(companyId, pId, page, size)
                .stream().map(ExpenseDTO::from).toList();

        long total = expenseRepository.countSearch(companyId, pId);
        return new PageResponse<>(items, page, size, total);
    }

    @Transactional
    public ExpenseDTO create(ExpenseCreateDTO dto) {
        Expense expense = new Expense();
        expense.id = UUID.randomUUID();
        expense.companyId = tenantContext.getCompanyId();
        expense.projectId = dto.projectId != null ? UUID.fromString(dto.projectId) : null;
        expense.category = dto.category;
        expense.amount = dto.amount;
        expense.expenseDate = dto.expenseDate;
        expense.description = dto.description;
        expense.createdAt = OffsetDateTime.now();

        expenseRepository.persist(expense);
        return ExpenseDTO.from(expense);
    }

    @Transactional
    public void delete(UUID id) {
        Expense expense = expenseRepository.findById(id);
        if (expense == null || !expense.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Expense not found");
        }
        expenseRepository.delete(expense);
    }
}
