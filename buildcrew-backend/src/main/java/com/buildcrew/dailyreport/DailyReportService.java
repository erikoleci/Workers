package com.buildcrew.dailyreport;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class DailyReportService {

    @Inject
    DailyReportRepository dailyReportRepository;

    @Inject
    DailyTargetRepository dailyTargetRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager em;

    public PageResponse<DailyReportDTO> search(String projectId, String crewId, LocalDate from, LocalDate to, int page, int size) {
        UUID pId = projectId != null ? UUID.fromString(projectId) : null;
        UUID cId = crewId != null ? UUID.fromString(crewId) : null;

        List<DailyReportDTO> items = dailyReportRepository.search(pId, cId, from, to, page, size)
                .stream().map(this::toDTO).toList();

        long total = dailyReportRepository.countSearch(pId, cId, from, to);
        return new PageResponse<>(items, page, size, total);
    }

    @Transactional
    public DailyReportDTO create(DailyReportCreateDTO dto) {
        UUID projectId = UUID.fromString(dto.projectId);
        UUID crewId = UUID.fromString(dto.crewId);

        DailyReport existing = dailyReportRepository.findByProjectCrewDate(projectId, crewId, dto.reportDate);
        if (existing != null) {
            throw new BadRequestException("Report already submitted for this project/crew/date");
        }

        DailyReport report = new DailyReport();
        report.id = UUID.randomUUID();
        report.projectId = projectId;
        report.crewId = crewId;
        report.reportDate = dto.reportDate;
        report.completedM2 = dto.completedM2;
        report.workedHours = dto.workedHours;
        report.comments = dto.comments;
        report.createdBy = tenantContext.getUserId();
        report.createdAt = OffsetDateTime.now();

        dailyReportRepository.persist(report);
        return toDTO(report);
    }

    @Transactional
    public DailyReportDTO update(UUID id, DailyReportCreateDTO dto) {
        DailyReport report = dailyReportRepository.findById(id);
        if (report == null) throw new NotFoundException("Report not found");

        report.completedM2 = dto.completedM2;
        report.workedHours = dto.workedHours;
        report.comments = dto.comments;

        return toDTO(report);
    }

    private DailyReportDTO toDTO(DailyReport r) {
        DailyReportDTO dto = new DailyReportDTO();
        dto.id = r.id.toString();
        dto.projectId = r.projectId.toString();
        dto.crewId = r.crewId.toString();
        dto.reportDate = r.reportDate;
        dto.completedM2 = r.completedM2;
        dto.workedHours = r.workedHours;
        dto.comments = r.comments;

        dto.projectName = lookupName("projects", "name", r.projectId);
        dto.crewName = lookupName("crews", "name", r.crewId);
        dto.createdByName = r.createdBy != null ? lookupName("users", "name", r.createdBy) : null;

        DailyTarget target = dailyTargetRepository.findByProjectAndDate(r.projectId, r.reportDate);
        BigDecimal targetM2 = target != null ? target.targetM2 : null;
        dto.targetM2 = targetM2;

        if (targetM2 != null && targetM2.compareTo(BigDecimal.ZERO) > 0) {
            dto.remainingM2 = targetM2.subtract(r.completedM2).max(BigDecimal.ZERO);
            dto.completionPercent = r.completedM2.multiply(BigDecimal.valueOf(100))
                    .divide(targetM2, 1, RoundingMode.HALF_UP);

            int pct = dto.completionPercent.intValue();
            if (pct >= 100) dto.status = "green";
            else if (pct >= 80) dto.status = "yellow";
            else dto.status = "red";
        } else {
            dto.status = "green"; // no target set — no comparison
        }

        return dto;
    }

    private String lookupName(String table, String column, UUID id) {
        Query q = em.createNativeQuery("SELECT " + column + " FROM " + table + " WHERE id = :id");
        q.setParameter("id", id);
        List<Object> result = q.getResultList();
        return result.isEmpty() ? null : (String) result.get(0);
    }
}
