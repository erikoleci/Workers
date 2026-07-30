package com.buildcrew.client;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ClientResource {

    @Inject
    ClientService clientService;

    @GET
    public PageResponse<ClientDTO> search(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return clientService.search(query, page, size);
    }

    @GET
    @Path("/{id}")
    public ClientDTO findById(@PathParam("id") UUID id) {
        return clientService.findById(id);
    }

    @POST
    @RolesAllowed({"owner", "manager"})
    public Response create(@Valid ClientCreateDTO dto) {
        ClientDTO created = clientService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public ClientDTO update(@PathParam("id") UUID id, @Valid ClientCreateDTO dto) {
        return clientService.update(id, dto);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"owner", "manager"})
    public Response delete(@PathParam("id") UUID id) {
        clientService.delete(id);
        return Response.noContent().build();
    }
}
