package com.buildcrew.worker;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/workers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class WorkerResource {

    @Inject
    WorkerService workerService;

    @GET
    public PageResponse<WorkerDTO> search(
            @QueryParam("query") String query,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return workerService.search(query, status, page, size);
    }

    @GET
    @Path("/{id}")
    public WorkerDTO findById(@PathParam("id") UUID id) {
        return workerService.findById(id);
    }

    @POST
    @RolesAllowed({"owner", "manager"})
    public Response create(@Valid WorkerCreateDTO dto) {
        WorkerDTO created = workerService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public WorkerDTO update(@PathParam("id") UUID id, @Valid WorkerCreateDTO dto) {
        return workerService.update(id, dto);
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"owner", "manager"})
    public Response toggleStatus(@PathParam("id") UUID id) {
        workerService.toggleStatus(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public Response delete(@PathParam("id") UUID id) {
        workerService.delete(id);
        return Response.noContent().build();
    }
}
