package com.buildcrew.user;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class UserResource {

    @Inject
    UserService userService;

    @GET
    public List<UserDTO> list(@QueryParam("role") String role) {
        return userService.list(role);
    }

    @POST
    @RolesAllowed("owner")
    public Response create(@Valid UserCreateDTO dto) {
        UserDTO created = userService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("owner")
    public Response deactivate(@PathParam("id") UUID id) {
        userService.deactivate(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}/reset-password")
    @RolesAllowed("owner")
    public Response resetPassword(@PathParam("id") UUID id, @Valid UserPasswordResetDTO dto) {
        userService.resetPassword(id, dto.newPassword);
        return Response.noContent().build();
    }
}
