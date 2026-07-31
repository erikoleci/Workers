package com.buildcrew.dailyreport;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyReportCreateDTO {

    @NotNull
    public String projectId;

    @NotNull
    public String crewId;

    @NotNull
    public LocalDate reportDate;

    @NotNull
    public BigDecimal completedM2;

    public BigDecimal workedHours;

    public String comments;
}
