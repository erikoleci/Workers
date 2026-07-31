package com.buildcrew.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollDTO {
    public String id;
    public String workerId;
    public String workerName;
    public LocalDate periodStart;
    public LocalDate periodEnd;
    public BigDecimal baseAmount;
    public BigDecimal bonuses;
    public BigDecimal deductions;
    public BigDecimal finalAmount;
    public String status;
}
