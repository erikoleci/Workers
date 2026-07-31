package com.buildcrew.crew;

import com.buildcrew.common.dto.PageResponse;
import com.buildcrew.security.TenantContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CrewService {

    @Inject
    CrewRepository crewRepository;

    @Inject
    CrewMemberRepository crewMemberRepository;

    @Inject
    TenantContext tenantContext;

    @Inject
    EntityManager em;

    @Transactional
    public PageResponse<CrewDTO> search(String query, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();

        List<Crew> crews = crewRepository.search(companyId, query, page, size);
        long total = crewRepository.countSearch(companyId, query);

        return new PageResponse<>(toDTOs(crews), page, size, total);
    }

    @Transactional
    public CrewDTO findById(UUID id) {
        return toDTOs(List.of(find(id))).get(0);
    }

    @Transactional
    public CrewDTO create(CrewCreateDTO dto) {
        Crew crew = new Crew();
        crew.id = UUID.randomUUID();
        crew.companyId = tenantContext.getCompanyId();
        crew.name = dto.name;
        crew.leaderId = dto.leaderId != null ? parseUuid("leaderId", dto.leaderId) : null;
        crew.currentProjectId = dto.currentProjectId != null ? parseUuid("currentProjectId", dto.currentProjectId) : null;
        crew.status = "active";
        crew.createdAt = OffsetDateTime.now();

        crewRepository.persist(crew);
        syncMembers(crew.id, dto.memberWorkerIds);

        return toDTOs(List.of(crew)).get(0);
    }

    @Transactional
    public CrewDTO update(UUID id, CrewCreateDTO dto) {
        Crew crew = find(id);
        crew.name = dto.name;
        crew.leaderId = dto.leaderId != null ? parseUuid("leaderId", dto.leaderId) : null;
        crew.currentProjectId = dto.currentProjectId != null ? parseUuid("currentProjectId", dto.currentProjectId) : null;

        syncMembers(crew.id, dto.memberWorkerIds);

        return toDTOs(List.of(crew)).get(0);
    }

    @Transactional
    public void deactivate(UUID id) {
        Crew crew = find(id);
        crew.status = "inactive";
    }

    private void syncMembers(UUID crewId, List<String> memberWorkerIds) {
        crewMemberRepository.removeAllFromCrew(crewId);
        if (memberWorkerIds == null) return;

        for (String workerIdStr : memberWorkerIds) {
            CrewMember member = new CrewMember();
            member.id = UUID.randomUUID();
            member.crewId = crewId;
            member.workerId = parseUuid("memberWorkerIds", workerIdStr);
            member.joinedAt = OffsetDateTime.now();
            crewMemberRepository.persist(member);
        }
    }

    private UUID parseUuid(String field, String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new jakarta.ws.rs.BadRequestException(field + " is not a valid id: " + value);
        }
    }

    private Crew find(UUID id) {
        Crew crew = crewRepository.findById(id);
        if (crew == null || !crew.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Crew not found");
        }
        return crew;
    }

    /**
     * Batched DTO mapping — avoids the N+1 query pattern of looking up each
     * leader/member name individually. For a page of 20 crews with 5 members
     * each, the naive per-row approach issues up to ~120 queries; this does 3
     * (crew_members for all crews, users for all leader ids, workers for all
     * member ids) regardless of how many crews are being mapped.
     */
    @SuppressWarnings("unchecked")
    private List<CrewDTO> toDTOs(List<Crew> crews) {
        if (crews.isEmpty()) return List.of();

        List<UUID> crewIds = crews.stream().map(c -> c.id).toList();

        List<CrewMember> allMembers = em.createQuery(
                        "select m from CrewMember m where m.crewId in :crewIds", CrewMember.class)
                .setParameter("crewIds", crewIds)
                .getResultList();

        List<UUID> leaderIds = crews.stream().map(c -> c.leaderId).filter(java.util.Objects::nonNull).distinct().toList();
        List<UUID> workerIds = allMembers.stream().map(m -> m.workerId).distinct().toList();

        java.util.Map<UUID, String> leaderNames = leaderIds.isEmpty() ? java.util.Map.of() : namesById("users", leaderIds);
        java.util.Map<UUID, String> workerNames = workerIds.isEmpty() ? java.util.Map.of() : namesById("workers", workerIds);

        java.util.Map<UUID, List<CrewMember>> membersByCrew = allMembers.stream()
                .collect(java.util.stream.Collectors.groupingBy(m -> m.crewId));

        return crews.stream().map(crew -> {
            CrewDTO dto = new CrewDTO();
            dto.id = crew.id.toString();
            dto.name = crew.name;
            dto.leaderId = crew.leaderId != null ? crew.leaderId.toString() : null;
            dto.leaderName = crew.leaderId != null ? leaderNames.get(crew.leaderId) : null;
            dto.currentProjectId = crew.currentProjectId != null ? crew.currentProjectId.toString() : null;
            dto.status = crew.status;

            dto.members = membersByCrew.getOrDefault(crew.id, List.of()).stream().map(m -> {
                CrewDTO.CrewMemberDTO memberDto = new CrewDTO.CrewMemberDTO();
                memberDto.workerId = m.workerId.toString();
                memberDto.workerName = workerNames.get(m.workerId);
                return memberDto;
            }).toList();

            return dto;
        }).toList();
    }

    private java.util.Map<UUID, String> namesById(String table, List<UUID> ids) {
        String nameColumn = table.equals("users") ? "name" : "full_name";
        Query q = em.createNativeQuery(
                "SELECT id, " + nameColumn + " FROM " + table + " WHERE id IN (:ids)");
        q.setParameter("ids", ids);
        List<Object[]> rows = q.getResultList();

        java.util.Map<UUID, String> result = new java.util.HashMap<>();
        for (Object[] row : rows) {
            result.put((UUID) row[0], (String) row[1]);
        }
        return result;
    }
}
