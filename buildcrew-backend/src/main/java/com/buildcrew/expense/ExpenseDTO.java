package com.buildcrew.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDTO {
    public String id;
    public String projectId;
    public String category;
    public BigDecimal amount;
    public LocalDate expenseDate;
    public String description;

    public static ExpenseDTO from(Expense e) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.id = e.id.toString();
        dto.projectId = e.projectId != null ? e.projectId.toString() : null;
        dto.category = e.category;
        dto.amount = e.amount;
        dto.expenseDate = e.expenseDate;
        dto.description = e.description;
        return dto;
    }
}
