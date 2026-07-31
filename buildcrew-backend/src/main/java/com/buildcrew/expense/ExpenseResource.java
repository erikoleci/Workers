package com.buildcrew.expense;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/api/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"owner", "manager"})
public class ExpenseResource {

    @Inject
    ExpenseService expenseService;

    @GET
    public PageResponse<ExpenseDTO> search(
            @QueryParam("projectId") String projectId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return expenseService.search(projectId, page, size);
    }

    @POST
    public Response create(@Valid ExpenseCreateDTO dto) {
        ExpenseDTO created = expenseService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        expenseService.delete(id);
        return Response.noContent().build();
    }
}
