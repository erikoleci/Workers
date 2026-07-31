package com.buildcrew.project;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository projectRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager em;

    public PageResponse<ProjectDTO> search(String query, String status, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();
        UUID restrictToLeaderId = "owner".equals(tenantContext.getRole()) ? null : tenantContext.getUserId();

        List<ProjectDTO> items = projectRepository.search(companyId, query, status, restrictToLeaderId, page, size)
                .stream().map(this::toDTO).toList();

        long total = projectRepository.countSearch(companyId, query, status, restrictToLeaderId);
        return new PageResponse<>(items, page, size, total);
    }

    public ProjectDTO findById(UUID id) {
        return toDTO(find(id));
    }

    @Transactional
    public ProjectDTO create(ProjectCreateDTO dto) {
        Project project = new Project();
        project.id = UUID.randomUUID();
        project.companyId = tenantContext.getCompanyId();
        applyFields(project, dto);
        project.status = "active";
        project.createdAt = OffsetDateTime.now();

        projectRepository.persist(project);
        return toDTO(project);
    }

    @Transactional
    public ProjectDTO update(UUID id, ProjectCreateDTO dto) {
        Project project = find(id);
        applyFields(project, dto);
        return toDTO(project);
    }

    @Transactional
    public ProjectDTO updateStatus(UUID id, String status) {
        Project project = find(id);
        project.status = status;
        return toDTO(project);
    }

    @Transactional
    public void delete(UUID id) {
        Project project = find(id);
        projectRepository.delete(project);
    }

    private void applyFields(Project project, ProjectCreateDTO dto) {
        project.clientId = UUID.fromString(dto.clientId);
        project.name = dto.name;
        project.address = dto.address;
        project.startDate = dto.startDate;
        project.deadline = dto.deadline;
        project.contractValue = dto.contractValue;
        project.totalM2 = dto.totalM2;
        project.assignedCrewId = dto.assignedCrewId != null ? UUID.fromString(dto.assignedCrewId) : null;
    }

    private Project find(UUID id) {
        Project project = projectRepository.findById(id);
        if (project == null || !project.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Project not found");
        }
        if (!"owner".equals(tenantContext.getRole()) && !isLedByCurrentUser(project.assignedCrewId)) {
            throw new NotFoundException("Project not found");
        }
        return project;
    }

    private boolean isLedByCurrentUser(UUID crewId) {
        if (crewId == null) return false;
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM crews WHERE id = :crewId AND leader_id = :userId")
                .setParameter("crewId", crewId)
                .setParameter("userId", tenantContext.getUserId())
                .getSingleResult();
        return count.longValue() > 0;
    }

    private ProjectDTO toDTO(Project p) {
        ProjectDTO dto = new ProjectDTO();
        dto.id = p.id.toString();
        dto.clientId = p.clientId != null ? p.clientId.toString() : null;
        dto.name = p.name;
        dto.address = p.address;
        dto.startDate = p.startDate;
        dto.deadline = p.deadline;
        dto.contractValue = p.contractValue;
        dto.totalM2 = p.totalM2;
        dto.assignedCrewId = p.assignedCrewId != null ? p.assignedCrewId.toString() : null;
        dto.status = p.status;

        dto.clientName = lookupName("clients", "company_name", p.clientId);
        dto.assignedCrewName = lookupName("crews", "name", p.assignedCrewId);

        dto.completedM2 = sumCompletedM2(p.id);
        dto.progressPercent = calculateProgress(dto.completedM2, p.totalM2);

        return dto;
    }

    private String lookupName(String table, String column, UUID id) {
        if (id == null) return null;
        Query q = em.createNativeQuery("SELECT " + column + " FROM " + table + " WHERE id = :id");
        q.setParameter("id", id);
        List<Object> result = q.getResultList();
        return result.isEmpty() ? null : (String) result.get(0);
    }

    private BigDecimal sumCompletedM2(UUID projectId) {
        Query q = em.createNativeQuery(
                "SELECT COALESCE(SUM(completed_m2), 0) FROM daily_reports WHERE project_id = :projectId");
        q.setParameter("projectId", projectId);
        return (BigDecimal) q.getSingleResult();
    }

    private BigDecimal calculateProgress(BigDecimal completed, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return completed.multiply(BigDecimal.valueOf(100))
                .divide(total, 1, RoundingMode.HALF_UP);
    }
}
