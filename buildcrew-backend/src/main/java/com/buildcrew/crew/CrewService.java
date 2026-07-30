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

    public PageResponse<CrewDTO> search(String query, int page, int size) {
        UUID companyId = tenantContext.getCompanyId();

        List<CrewDTO> items = crewRepository.search(companyId, query, page, size)
                .stream().map(this::toDTO).toList();

        long total = crewRepository.countSearch(companyId, query);
        return new PageResponse<>(items, page, size, total);
    }

    public CrewDTO findById(UUID id) {
        return toDTO(find(id));
    }

    @Transactional
    public CrewDTO create(CrewCreateDTO dto) {
        Crew crew = new Crew();
        crew.id = UUID.randomUUID();
        crew.companyId = tenantContext.getCompanyId();
        crew.name = dto.name;
        crew.leaderId = dto.leaderId != null ? UUID.fromString(dto.leaderId) : null;
        crew.currentProjectId = dto.currentProjectId != null ? UUID.fromString(dto.currentProjectId) : null;
        crew.status = "active";
        crew.createdAt = OffsetDateTime.now();

        crewRepository.persist(crew);
        syncMembers(crew.id, dto.memberWorkerIds);

        return toDTO(crew);
    }

    @Transactional
    public CrewDTO update(UUID id, CrewCreateDTO dto) {
        Crew crew = find(id);
        crew.name = dto.name;
        crew.leaderId = dto.leaderId != null ? UUID.fromString(dto.leaderId) : null;
        crew.currentProjectId = dto.currentProjectId != null ? UUID.fromString(dto.currentProjectId) : null;

        syncMembers(crew.id, dto.memberWorkerIds);

        return toDTO(crew);
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
            member.workerId = UUID.fromString(workerIdStr);
            member.joinedAt = OffsetDateTime.now();
            crewMemberRepository.persist(member);
        }
    }

    private Crew find(UUID id) {
        Crew crew = crewRepository.findById(id);
        if (crew == null || !crew.companyId.equals(tenantContext.getCompanyId())) {
            throw new NotFoundException("Crew not found");
        }
        return crew;
    }

    @SuppressWarnings("unchecked")
    private CrewDTO toDTO(Crew crew) {
        CrewDTO dto = new CrewDTO();
        dto.id = crew.id.toString();
        dto.name = crew.name;
        dto.leaderId = crew.leaderId != null ? crew.leaderId.toString() : null;
        dto.currentProjectId = crew.currentProjectId != null ? crew.currentProjectId.toString() : null;
        dto.status = crew.status;

        if (crew.leaderId != null) {
            Query q = em.createNativeQuery("SELECT name FROM users WHERE id = :id");
            q.setParameter("id", crew.leaderId);
            List<Object> result = q.getResultList();
            dto.leaderName = result.isEmpty() ? null : (String) result.get(0);
        }

        List<CrewMember> members = crewMemberRepository.findByCrew(crew.id);
        dto.members = members.stream().map(m -> {
            CrewDTO.CrewMemberDTO memberDto = new CrewDTO.CrewMemberDTO();
            memberDto.workerId = m.workerId.toString();

            Query q = em.createNativeQuery("SELECT full_name FROM workers WHERE id = :id");
            q.setParameter("id", m.workerId);
            List<Object> result = q.getResultList();
            memberDto.workerName = result.isEmpty() ? null : (String) result.get(0);

            return memberDto;
        }).toList();

        return dto;
    }
}
