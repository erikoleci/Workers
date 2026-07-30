package com.buildcrew.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkerCreateDTO {

    @NotBlank
    public String fullName;

    public String phone;

    public String position;

    @NotNull
    @Pattern(regexp = "daily|per_m2", message = "payType must be 'daily' or 'per_m2'")
    public String payType;

    public BigDecimal dailySalary;

    public BigDecimal pricePerM2;

    public LocalDate employmentDate;
}
