package com.buildcrew.dailyreport;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/daily-targets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@ApplicationScoped
public class DailyTargetResource {

    @Inject
    DailyTargetRepository dailyTargetRepository;

    @GET
    @Path("/project/{projectId}")
    public List<DailyTargetDTO> findByProject(@PathParam("projectId") UUID projectId) {
        return dailyTargetRepository.findByProject(projectId).stream().map(t -> {
            DailyTargetDTO dto = new DailyTargetDTO();
            dto.id = t.id.toString();
            dto.projectId = t.projectId.toString();
            dto.targetDate = t.targetDate;
            dto.targetM2 = t.targetM2;
            return dto;
        }).toList();
    }

    @POST
    @RolesAllowed({"owner", "manager"})
    @Transactional
    public Response create(@Valid DailyTargetCreateDTO dto) {
        UUID projectId = UUID.fromString(dto.projectId);
        DailyTarget target = dailyTargetRepository.findByProjectAndDate(projectId, dto.targetDate);

        if (target == null) {
            target = new DailyTarget();
            target.id = UUID.randomUUID();
            target.projectId = projectId;
            target.targetDate = dto.targetDate;
            target.targetM2 = dto.targetM2;
            dailyTargetRepository.persist(target);
        } else {
            target.targetM2 = dto.targetM2;
        }

        DailyTargetDTO result = new DailyTargetDTO();
        result.id = target.id.toString();
        result.projectId = target.projectId.toString();
        result.targetDate = target.targetDate;
        result.targetM2 = target.targetM2;

        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}
