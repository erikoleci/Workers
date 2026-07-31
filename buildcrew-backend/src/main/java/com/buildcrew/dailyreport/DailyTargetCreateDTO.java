package com.buildcrew.dailyreport;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTargetCreateDTO {

    @NotNull
    public String projectId;

    @NotNull
    public LocalDate targetDate;

    @NotNull
    public BigDecimal targetM2;
}
