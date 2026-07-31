package com.buildcrew.reports;

import java.math.BigDecimal;

public class ReportDTOs {

    public static class ProductionByWorker {
        public String workerId;
        public String workerName;
        public BigDecimal totalM2;
        public long daysWorked;
    }

    public static class ProductionByCrew {
        public String crewId;
        public String crewName;
        public BigDecimal totalM2;
        public long reportsCount;
    }

    public static class ProductionByProject {
        public String projectId;
        public String projectName;
        public BigDecimal totalM2;
        public BigDecimal targetM2;
        public BigDecimal progressPercent;
    }

    public static class MonthlyProduction {
        public String month; // YYYY-MM
        public BigDecimal totalM2;
    }

    public static class FinancialSummary {
        public BigDecimal revenue;
        public BigDecimal expenses;
        public BigDecimal payroll;
        public BigDecimal profit;
    }
}
