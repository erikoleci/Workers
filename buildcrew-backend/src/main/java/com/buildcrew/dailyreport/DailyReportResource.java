package com.buildcrew.dailyreport;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.UUID;

@Path("/api/daily-reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class DailyReportResource {

    @Inject
    DailyReportService dailyReportService;

    @GET
    public PageResponse<DailyReportDTO> search(
            @QueryParam("projectId") String projectId,
            @QueryParam("crewId") String crewId,
            @QueryParam("from") LocalDate from,
            @QueryParam("to") LocalDate to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return dailyReportService.search(projectId, crewId, from, to, page, size);
    }

    @POST
    @RolesAllowed({"owner", "manager", "crew_leader"})
    public Response create(@Valid DailyReportCreateDTO dto) {
        DailyReportDTO created = dailyReportService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"owner", "manager", "crew_leader"})
    public DailyReportDTO update(@PathParam("id") UUID id, @Valid DailyReportCreateDTO dto) {
        return dailyReportService.update(id, dto);
    }
}
