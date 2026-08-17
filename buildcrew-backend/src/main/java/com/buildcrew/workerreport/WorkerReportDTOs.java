package com.buildcrew.workerreport;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class WorkerReportDTOs {

    public static class SubmitDTO {
        @NotBlank
        public String projectId;

        @NotNull
        public LocalDate reportDate;

        @NotNull
        @DecimalMin(value = "0", inclusive = true, message = "m² can't be negative")
        public BigDecimal completedM2;

        public String comments;
    }

    public static class ProjectOptionDTO {
        public String projectId;
        public String projectName;
    }

    public static class ReportDTO {
        public String id;
        public String projectId;
        public String projectName;
        public LocalDate reportDate;
        public BigDecimal completedM2;
        public String comments;
    }
}
