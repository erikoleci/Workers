package com.buildcrew.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectCreateDTO {

    @NotNull
    public String clientId;

    @NotBlank
    public String name;

    public String address;

    public LocalDate startDate;

    public LocalDate deadline;

    public BigDecimal contractValue;

    public BigDecimal totalM2;

    public String assignedCrewId;
}
