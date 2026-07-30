package com.buildcrew.worker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkerDTO {
    public String id;
    public String fullName;
    public String phone;
    public String position;
    public String payType;
    public BigDecimal dailySalary;
    public BigDecimal pricePerM2;
    public LocalDate employmentDate;
    public String status;

    public static WorkerDTO from(Worker w) {
        WorkerDTO dto = new WorkerDTO();
        dto.id = w.id.toString();
        dto.fullName = w.fullName;
        dto.phone = w.phone;
        dto.position = w.position;
        dto.payType = w.payType;
        dto.dailySalary = w.dailySalary;
        dto.pricePerM2 = w.pricePerM2;
        dto.employmentDate = w.employmentDate;
        dto.status = w.status;
        return dto;
    }
}
