package com.buildcrew.dashboard;

import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DashboardService {

    @Inject
    EntityManager em;

    @Inject
    TenantContext tenantContext;

    @Transactional
    public DashboardSummaryDTO getSummary() {
        UUID companyId = tenantContext.getCompanyId();
        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        dto.activeProjects = countActive("projects", companyId);
        dto.activeCrews = countActive("crews", companyId);
        dto.activeWorkers = countActive("workers", companyId);

        dto.todayProduction = sumProduction(companyId, "CURRENT_DATE");
        dto.weeklyProduction = sumProduction(companyId, "CURRENT_DATE - INTERVAL '7 days'");
        dto.monthlyProduction = sumProduction(companyId, "CURRENT_DATE - INTERVAL '30 days'");

        dto.revenue = sumRevenue(companyId);
        dto.payrollPending = sumPayrollPending(companyId);

        dto.delayedProjects = findDelayedProjects(companyId);
        dto.notifications = findRecentNotifications(companyId);

        return dto;
    }

    private long countActive(String table, UUID companyId) {
        Query q = em.createNativeQuery(
                "SELECT COUNT(*) FROM " + table + " WHERE company_id = :companyId AND status = 'active'");
        q.setParameter("companyId", companyId);
        return ((Number) q.getSingleResult()).longValue();
    }

    private BigDecimal sumProduction(UUID companyId, String sinceExpr) {
        Query q = em.createNativeQuery(
                "SELECT COALESCE(SUM(dr.completed_m2), 0) FROM daily_reports dr " +
                "JOIN projects p ON p.id = dr.project_id " +
                "WHERE p.company_id = :companyId AND dr.report_date >= " + sinceExpr);
        q.setParameter("companyId", companyId);
        return (BigDecimal) q.getSingleResult();
    }

    private BigDecimal sumRevenue(UUID companyId) {
        Query q = em.createNativeQuery(
                "SELECT COALESCE(SUM(contract_value), 0) FROM projects " +
                "WHERE company_id = :companyId AND status IN ('active','completed')");
        q.setParameter("companyId", companyId);
        return (BigDecimal) q.getSingleResult();
    }

    private BigDecimal sumPayrollPending(UUID companyId) {
        Query q = em.createNativeQuery(
                "SELECT COALESCE(SUM(final_amount), 0) FROM payroll " +
                "WHERE company_id = :companyId AND status = 'pending'");
        q.setParameter("companyId", companyId);
        return (BigDecimal) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private List<DashboardSummaryDTO.DelayedProjectDTO> findDelayedProjects(UUID companyId) {
        Query q = em.createNativeQuery(
                "SELECT p.id, p.name, p.deadline, p.total_m2, " +
                "COALESCE((SELECT SUM(dr.completed_m2) FROM daily_reports dr WHERE dr.project_id = p.id), 0) AS completed " +
                "FROM projects p " +
                "WHERE p.company_id = :companyId AND p.status = 'delayed' " +
                "ORDER BY p.deadline ASC LIMIT 10");
        q.setParameter("companyId", companyId);
        List<Object[]> rows = q.getResultList();

        return rows.stream().map(r -> {
            DashboardSummaryDTO.DelayedProjectDTO d = new DashboardSummaryDTO.DelayedProjectDTO();
            d.id = r[0].toString();
            d.name = (String) r[1];
            d.deadline = r[2] != null ? r[2].toString() : null;

            BigDecimal totalM2 = (BigDecimal) r[3];
            BigDecimal completedM2 = (BigDecimal) r[4];
            d.progressPercent = (totalM2 != null && totalM2.compareTo(BigDecimal.ZERO) > 0)
                    ? completedM2.multiply(BigDecimal.valueOf(100)).divide(totalM2, 0, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            return d;
        }).toList();
    }

    @SuppressWarnings("unchecked")
    private List<DashboardSummaryDTO.NotificationDTO> findRecentNotifications(UUID companyId) {
        Query q = em.createNativeQuery(
                "SELECT id, type, message, is_read, created_at FROM notifications " +
                "WHERE company_id = :companyId ORDER BY created_at DESC LIMIT 10");
        q.setParameter("companyId", companyId);
        List<Object[]> rows = q.getResultList();

        return rows.stream().map(r -> {
            DashboardSummaryDTO.NotificationDTO n = new DashboardSummaryDTO.NotificationDTO();
            n.id = r[0].toString();
            n.type = (String) r[1];
            n.message = (String) r[2];
            n.isRead = (Boolean) r[3];
            n.createdAt = r[4].toString();
            return n;
        }).toList();
    }
}
