package com.buildcrew.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkerCreateDTO {

    @NotBlank
    public String fullName;

    public String phone;

    public String position;

    @NotNull
    public String payType; // daily | per_m2

    public BigDecimal dailySalary;

    public BigDecimal pricePerM2;

    public LocalDate employmentDate;
}
