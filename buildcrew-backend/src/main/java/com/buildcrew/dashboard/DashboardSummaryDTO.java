package com.buildcrew.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDTO {

    public long activeProjects;
    public long activeCrews;
    public long activeWorkers;

    public BigDecimal todayProduction;
    public BigDecimal weeklyProduction;
    public BigDecimal monthlyProduction;

    public BigDecimal revenue;
    public BigDecimal payrollPending;

    public List<DelayedProjectDTO> delayedProjects;
    public List<NotificationDTO> notifications;

    public static class DelayedProjectDTO {
        public String id;
        public String name;
        public String deadline;
        public BigDecimal progressPercent;
    }

    public static class NotificationDTO {
        public String id;
        public String type;
        public String message;
        public boolean isRead;
        public String createdAt;
    }
}
