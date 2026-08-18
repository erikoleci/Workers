package com.buildcrew.workerreport;

import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkerReportService {

    @Inject
    WorkerDailyReportRepository repository;

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager em;

    /** Everything the worker's own report page needs in one call: pay type, active projects, today's target. */
    @SuppressWarnings("unchecked")
    public WorkerReportDTOs.WorkerContextDTO myContext() {
        UUID workerId = tenantContext.getUserId();

        Query payQ = em.createNativeQuery("SELECT pay_type FROM workers WHERE id = :id");
        payQ.setParameter("id", workerId);
        List<Object> payResult = payQ.getResultList();

        WorkerReportDTOs.WorkerContextDTO ctx = new WorkerReportDTOs.WorkerContextDTO();
        ctx.payType = payResult.isEmpty() ? null : (String) payResult.get(0);
        ctx.projects = myProjects();

        WorkerDailyTarget target = WorkerDailyTarget.find(workerId, LocalDate.now());
        if (target != null) {
            WorkerReportDTOs.TargetDTO t = new WorkerReportDTOs.TargetDTO();
            t.targetDate = target.targetDate;
            t.targetM2 = target.targetM2;
            ctx.todayTarget = t;
        }

        return ctx;
    }

    /** The active project(s) the logged-in worker's crew is currently assigned to. */
    @SuppressWarnings("unchecked")
    public List<WorkerReportDTOs.ProjectOptionDTO> myProjects() {
        Query q = em.createNativeQuery(
                "SELECT DISTINCT p.id, p.name FROM projects p " +
                "JOIN crews c ON c.current_project_id = p.id " +
                "JOIN crew_members cm ON cm.crew_id = c.id " +
                "WHERE cm.worker_id = :workerId AND p.status = 'active'");
        q.setParameter("workerId", tenantContext.getUserId());

        List<Object[]> rows = q.getResultList();
        return rows.stream().map(r -> {
            WorkerReportDTOs.ProjectOptionDTO dto = new WorkerReportDTOs.ProjectOptionDTO();
            dto.projectId = r[0].toString();
            dto.projectName = (String) r[1];
            return dto;
        }).toList();
    }

    @Transactional
    public WorkerReportDTOs.ReportDTO submit(WorkerReportDTOs.SubmitDTO dto) {
        UUID workerId = tenantContext.getUserId();
        UUID projectId = UUID.fromString(dto.projectId);

        if (!isAssignedToProject(workerId, projectId)) {
            throw new ForbiddenException("You are not assigned to that project");
        }

        WorkerDailyReport existing = repository.findByWorkerProjectDate(workerId, projectId, dto.reportDate);
        if (existing != null) {
            throw new BadRequestException("You already submitted a report for this project/date");
        }

        WorkerDailyReport report = new WorkerDailyReport();
        report.id = UUID.randomUUID();
        report.workerId = workerId;
        report.projectId = projectId;
        report.reportDate = dto.reportDate;
        report.completedM2 = dto.completedM2;
        report.comments = dto.comments;
        report.createdAt = java.time.OffsetDateTime.now();

        repository.persist(report);
        return toDTO(report);
    }

    public List<WorkerReportDTOs.ReportDTO> myReports(int page, int size) {
        UUID workerId = tenantContext.getUserId();
        return repository.findByWorker(workerId, page, size).stream().map(this::toDTO).toList();
    }

    private boolean isAssignedToProject(UUID workerId, UUID projectId) {
        Query q = em.createNativeQuery(
                "SELECT COUNT(*) FROM crew_members cm " +
                "JOIN crews c ON c.id = cm.crew_id " +
                "WHERE cm.worker_id = :workerId AND c.current_project_id = :projectId");
        q.setParameter("workerId", workerId);
        q.setParameter("projectId", projectId);
        Number count = (Number) q.getSingleResult();
        return count.longValue() > 0;
    }

    private WorkerReportDTOs.ReportDTO toDTO(WorkerDailyReport r) {
        WorkerReportDTOs.ReportDTO dto = new WorkerReportDTOs.ReportDTO();
        dto.id = r.id.toString();
        dto.projectId = r.projectId.toString();
        dto.reportDate = r.reportDate;
        dto.completedM2 = r.completedM2;
        dto.comments = r.comments;

        Query q = em.createNativeQuery("SELECT name FROM projects WHERE id = :id");
        q.setParameter("id", r.projectId);
        List<Object> result = q.getResultList();
        dto.projectName = result.isEmpty() ? null : (String) result.get(0);

        return dto;
    }
}
