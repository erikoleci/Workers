package com.buildcrew.project;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @GET
    public PageResponse<ProjectDTO> search(
            @QueryParam("query") String query,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return projectService.search(query, status, page, size);
    }

    @GET
    @Path("/{id}")
    public ProjectDTO findById(@PathParam("id") UUID id) {
        return projectService.findById(id);
    }

    @POST
    @RolesAllowed({"owner", "manager"})
    public Response create(@Valid ProjectCreateDTO dto) {
        ProjectDTO created = projectService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public ProjectDTO update(@PathParam("id") UUID id, @Valid ProjectCreateDTO dto) {
        return projectService.update(id, dto);
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"owner", "manager"})
    public ProjectDTO updateStatus(@PathParam("id") UUID id, @Valid ProjectStatusUpdateDTO dto) {
        return projectService.updateStatus(id, dto.status);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public Response delete(@PathParam("id") UUID id) {
        projectService.delete(id);
        return Response.noContent().build();
    }
}
