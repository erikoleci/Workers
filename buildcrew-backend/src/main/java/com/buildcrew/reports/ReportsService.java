package com.buildcrew.reports;

import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ReportsService {

    @Inject
    EntityManager em;

    @Inject
    TenantContext tenantContext;

    @SuppressWarnings("unchecked")
    public List<ReportDTOs.ProductionByWorker> productionByWorker(LocalDate from, LocalDate to) {
        UUID companyId = tenantContext.getCompanyId();
        Query q = em.createNativeQuery(
                "SELECT w.id, w.full_name, w.pay_type, w.daily_salary, w.price_per_m2, " +
                // per_m2 workers: their own individually-reported m2 (no double-counting)
                "COALESCE((SELECT SUM(wdr.completed_m2) FROM worker_daily_reports wdr " +
                "  WHERE wdr.worker_id = w.id AND wdr.report_date BETWEEN :from AND :to), 0) AS period_m2, " +
                // daily-pay workers: days_worked = distinct days their crew filed a report (attendance proxy)
                "COALESCE((SELECT COUNT(DISTINCT dr.report_date) FROM daily_reports dr " +
                "  JOIN crew_members cm ON cm.crew_id = dr.crew_id " +
                "  WHERE cm.worker_id = w.id AND dr.report_date BETWEEN :from AND :to), 0) AS days_worked, " +
                "COALESCE((SELECT SUM(wdr.completed_m2) FROM worker_daily_reports wdr " +
                "  WHERE wdr.worker_id = w.id AND wdr.report_date = CURRENT_DATE), 0) AS today_m2 " +
                "FROM workers w " +
                "WHERE w.company_id = :companyId " +
                "ORDER BY w.full_name");
        q.setParameter("companyId", companyId);
        q.setParameter("from", from);
        q.setParameter("to", to);

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> {
            ReportDTOs.ProductionByWorker dto = new ReportDTOs.ProductionByWorker();
            dto.workerId = r[0].toString();
            dto.workerName = (String) r[1];
            dto.payType = (String) r[2];
            BigDecimal dailySalary = (BigDecimal) r[3];
            BigDecimal pricePerM2 = (BigDecimal) r[4];
            dto.totalM2 = (BigDecimal) r[5];
            dto.daysWorked = ((Number) r[6]).longValue();
            dto.todayM2 = (BigDecimal) r[7];

            if ("daily".equals(dto.payType)) {
                dto.estimatedPayment = dailySalary != null
                        ? dailySalary.multiply(BigDecimal.valueOf(dto.daysWorked))
                        : BigDecimal.ZERO;
            } else {
                dto.estimatedPayment = pricePerM2 != null
                        ? pricePerM2.multiply(dto.totalM2)
                        : BigDecimal.ZERO;
            }

            return dto;
        }).toList();
    }

    @SuppressWarnings("unchecked")
    public List<ReportDTOs.ProductionByCrew> productionByCrew(LocalDate from, LocalDate to) {
        UUID companyId = tenantContext.getCompanyId();
        Query q = em.createNativeQuery(
                "SELECT c.id, c.name, COALESCE(SUM(dr.completed_m2), 0), COUNT(dr.id) " +
                "FROM crews c " +
                "JOIN daily_reports dr ON dr.crew_id = c.id " +
                "WHERE c.company_id = :companyId AND dr.report_date BETWEEN :from AND :to " +
                "GROUP BY c.id, c.name ORDER BY c.name");
        q.setParameter("companyId", companyId);
        q.setParameter("from", from);
        q.setParameter("to", to);

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> {
            ReportDTOs.ProductionByCrew dto = new ReportDTOs.ProductionByCrew();
            dto.crewId = r[0].toString();
            dto.crewName = (String) r[1];
            dto.totalM2 = (BigDecimal) r[2];
            dto.reportsCount = ((Number) r[3]).longValue();
            return dto;
        }).toList();
    }

    @SuppressWarnings("unchecked")
    public List<ReportDTOs.ProductionByProject> productionByProject() {
        UUID companyId = tenantContext.getCompanyId();
        Query q = em.createNativeQuery(
                "SELECT p.id, p.name, p.total_m2, " +
                "COALESCE((SELECT SUM(dr.completed_m2) FROM daily_reports dr WHERE dr.project_id = p.id), 0) + " +
                "COALESCE((SELECT SUM(wdr.completed_m2) FROM worker_daily_reports wdr WHERE wdr.project_id = p.id " +
                "  AND NOT EXISTS (SELECT 1 FROM daily_reports dr2 WHERE dr2.project_id = p.id AND dr2.report_date = wdr.report_date)" +
                "), 0) " +
                "FROM projects p WHERE p.company_id = :companyId ORDER BY p.name");
        q.setParameter("companyId", companyId);

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> {
            ReportDTOs.ProductionByProject dto = new ReportDTOs.ProductionByProject();
            dto.projectId = r[0].toString();
            dto.projectName = (String) r[1];
            dto.targetM2 = (BigDecimal) r[2];
            dto.totalM2 = (BigDecimal) r[3];
            dto.progressPercent = (dto.targetM2 != null && dto.targetM2.compareTo(BigDecimal.ZERO) > 0)
                    ? dto.totalM2.multiply(BigDecimal.valueOf(100)).divide(dto.targetM2, 1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            return dto;
        }).toList();
    }

    @SuppressWarnings("unchecked")
    public List<ReportDTOs.MonthlyProduction> monthlyProduction(int months) {
        UUID companyId = tenantContext.getCompanyId();
        Query q = em.createNativeQuery(
                "WITH crew_side AS (" +
                "  SELECT project_id, report_date, SUM(completed_m2) AS m2 FROM daily_reports GROUP BY project_id, report_date" +
                "), worker_side AS (" +
                "  SELECT project_id, report_date, SUM(completed_m2) AS m2 FROM worker_daily_reports GROUP BY project_id, report_date" +
                "), per_day AS (" +
                "  SELECT project_id, report_date, GREATEST(COALESCE(c.m2,0), COALESCE(w.m2,0)) AS m2 " +
                "  FROM crew_side c FULL OUTER JOIN worker_side w USING (project_id, report_date)" +
                ") " +
                "SELECT to_char(date_trunc('month', pd.report_date), 'YYYY-MM') AS month, " +
                "COALESCE(SUM(pd.m2), 0) " +
                "FROM per_day pd JOIN projects p ON p.id = pd.project_id " +
                "WHERE p.company_id = :companyId " +
                "AND pd.report_date >= CURRENT_DATE - (:months || ' months')::interval " +
                "GROUP BY month ORDER BY month");
        q.setParameter("companyId", companyId);
        q.setParameter("months", months);

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> {
            ReportDTOs.MonthlyProduction dto = new ReportDTOs.MonthlyProduction();
            dto.month = (String) r[0];
            dto.totalM2 = (BigDecimal) r[1];
            return dto;
        }).toList();
    }

    public ReportDTOs.FinancialSummary financialSummary(LocalDate from, LocalDate to) {
        UUID companyId = tenantContext.getCompanyId();
        ReportDTOs.FinancialSummary dto = new ReportDTOs.FinancialSummary();

        Query revenueQ = em.createNativeQuery(
                "SELECT COALESCE(SUM(contract_value), 0) FROM projects " +
                "WHERE company_id = :companyId AND status IN ('active','completed')");
        revenueQ.setParameter("companyId", companyId);
        dto.revenue = (BigDecimal) revenueQ.getSingleResult();

        Query expensesQ = em.createNativeQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                "WHERE company_id = :companyId AND expense_date BETWEEN :from AND :to");
        expensesQ.setParameter("companyId", companyId);
        expensesQ.setParameter("from", from);
        expensesQ.setParameter("to", to);
        dto.expenses = (BigDecimal) expensesQ.getSingleResult();

        Query payrollQ = em.createNativeQuery(
                "SELECT COALESCE(SUM(final_amount), 0) FROM payroll " +
                "WHERE company_id = :companyId AND period_start >= :from AND period_end <= :to");
        payrollQ.setParameter("companyId", companyId);
        payrollQ.setParameter("from", from);
        payrollQ.setParameter("to", to);
        dto.payroll = (BigDecimal) payrollQ.getSingleResult();

        dto.profit = dto.revenue.subtract(dto.expenses).subtract(dto.payroll);

        return dto;
    }
}
