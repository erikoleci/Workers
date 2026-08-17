package com.buildcrew.workerreport;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/worker-reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"worker"})
public class WorkerReportResource {

    @Inject
    WorkerReportService service;

    @GET
    @Path("/my-projects")
    public List<WorkerReportDTOs.ProjectOptionDTO> myProjects() {
        return service.myProjects();
    }

    @POST
    public Response submit(@Valid WorkerReportDTOs.SubmitDTO dto) {
        WorkerReportDTOs.ReportDTO created = service.submit(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/mine")
    public List<WorkerReportDTOs.ReportDTO> myReports(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return service.myReports(page, size);
    }
}
