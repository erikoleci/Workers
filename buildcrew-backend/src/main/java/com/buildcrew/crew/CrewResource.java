package com.buildcrew.crew;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/crews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class CrewResource {

    @Inject
    CrewService crewService;

    @GET
    public PageResponse<CrewDTO> search(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return crewService.search(query, page, size);
    }

    @GET
    @Path("/{id}")
    public CrewDTO findById(@PathParam("id") UUID id) {
        return crewService.findById(id);
    }

    @POST
    @RolesAllowed({"owner", "manager"})
    public Response create(@Valid CrewCreateDTO dto) {
        CrewDTO created = crewService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public CrewDTO update(@PathParam("id") UUID id, @Valid CrewCreateDTO dto) {
        return crewService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public Response deactivate(@PathParam("id") UUID id) {
        crewService.deactivate(id);
        return Response.noContent().build();
    }
}
