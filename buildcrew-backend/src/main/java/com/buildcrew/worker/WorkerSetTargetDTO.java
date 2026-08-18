package com.buildcrew.worker;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkerSetTargetDTO {

    @NotNull
    public LocalDate targetDate;

    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    public BigDecimal targetM2;
}
