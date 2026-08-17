package com.buildcrew.notification;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificationScheduler {

    @Inject
    EntityManager em;

    @Inject
    NotificationService notificationService;

    /**
     * Runs once a day at 18:00 server time. Checks, per company:
     *  - active crews (assigned to an active project) that haven't
     *    submitted today's daily report
     *  - active projects whose deadline is within the next 3 days
     * and creates a notification for each owner/manager of that company.
     */
    @Scheduled(cron = "0 0 18 * * ?")
    @Transactional
    public void runDailyChecks() {
        checkMissingReports();
        checkUpcomingDeadlines();
    }

    @SuppressWarnings("unchecked")
    private void checkMissingReports() {
        Query q = em.createNativeQuery(
                "SELECT c.id, c.company_id, c.name " +
                "FROM crews c " +
                "JOIN projects p ON p.id = c.current_project_id " +
                "WHERE c.status = 'active' AND p.status = 'active' " +
                "AND NOT EXISTS (" +
                "  SELECT 1 FROM daily_reports dr " +
                "  WHERE dr.crew_id = c.id AND dr.report_date = CURRENT_DATE" +
                ")");

        List<Object[]> rows = q.getResultList();
        for (Object[] row : rows) {
            UUID companyId = (UUID) row[1];
            String crewName = (String) row[2];
            notificationService.notifyCompanyManagers(
                    companyId,
                    "missing_report",
                    "Crew \"" + crewName + "\" has not submitted today's report"
            );
        }
    }

    @SuppressWarnings("unchecked")
    private void checkUpcomingDeadlines() {
        Query q = em.createNativeQuery(
                "SELECT id, company_id, name, deadline FROM projects " +
                "WHERE status = 'active' " +
                "AND deadline IS NOT NULL " +
                "AND deadline BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '3 days'");

        List<Object[]> rows = q.getResultList();
        for (Object[] row : rows) {
            UUID companyId = (UUID) row[1];
            String projectName = (String) row[2];
            Object deadline = row[3];
            notificationService.notifyCompanyManagers(
                    companyId,
                    "deadline_close",
                    "Project \"" + projectName + "\" deadline is close (" + deadline + ")"
            );
        }
    }
}
