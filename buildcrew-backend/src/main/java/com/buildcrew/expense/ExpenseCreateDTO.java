package com.buildcrew.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseCreateDTO {

    public String projectId;

    @NotBlank
    public String category;

    @NotNull
    public BigDecimal amount;

    @NotNull
    public LocalDate expenseDate;

    public String description;
}
