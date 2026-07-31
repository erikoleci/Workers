package com.buildcrew.payroll;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class PayrollGenerateDTO {

    @NotNull
    public LocalDate periodStart;

    @NotNull
    public LocalDate periodEnd;

    // Optional — if empty, generates for all active workers
    public List<String> workerIds;
}
