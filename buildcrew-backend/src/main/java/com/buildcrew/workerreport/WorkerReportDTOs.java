package com.buildcrew.workerreport;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    public static class SetTargetDTO {
        @NotNull
        public LocalDate targetDate;

        @NotNull
        @DecimalMin(value = "0", inclusive = true)
        public BigDecimal targetM2;
    }

    public static class TargetDTO {
        public LocalDate targetDate;
        public BigDecimal targetM2;
    }

    public static class WorkerContextDTO {
        public String payType; // "daily" or "per_m2"
        public List<ProjectOptionDTO> projects;
        public TargetDTO todayTarget; // null if none set
    }
}
