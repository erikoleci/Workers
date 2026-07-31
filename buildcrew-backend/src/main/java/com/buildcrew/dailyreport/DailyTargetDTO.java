package com.buildcrew.dailyreport;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTargetDTO {
    public String id;
    public String projectId;
    public LocalDate targetDate;
    public BigDecimal targetM2;
}
