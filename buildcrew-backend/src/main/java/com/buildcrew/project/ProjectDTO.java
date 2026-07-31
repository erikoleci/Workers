package com.buildcrew.project;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectDTO {
    public String id;
    public String clientId;
    public String clientName;
    public String name;
    public String address;
    public LocalDate startDate;
    public LocalDate deadline;
    public BigDecimal contractValue;
    public BigDecimal totalM2;
    public String assignedCrewId;
    public String assignedCrewName;
    public String status;

    // Progress fields (computed)
    public BigDecimal completedM2;
    public BigDecimal progressPercent;
}
