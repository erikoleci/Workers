package com.buildcrew.dailyreport;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyReportDTO {
    public String id;
    public String projectId;
    public String projectName;
    public String crewId;
    public String crewName;
    public LocalDate reportDate;
    public BigDecimal completedM2;
    public BigDecimal workedHours;
    public String comments;
    public String createdByName;

    // Target comparison
    public BigDecimal targetM2;
    public BigDecimal remainingM2;
    public BigDecimal completionPercent;
    public String status; // green | yellow | red
}
