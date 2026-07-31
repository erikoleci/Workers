package com.buildcrew.payroll;

import com.buildcrew.common.dto.PageResponse;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/payroll")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"owner", "manager"})
public class PayrollResource {

    @Inject
    PayrollService payrollService;

    @GET
    public PageResponse<PayrollDTO> search(
            @QueryParam("status") String status,
            @QueryParam("workerId") String workerId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return payrollService.search(status, workerId, page, size);
    }

    @POST
    @Path("/generate")
    public List<PayrollDTO> generate(@Valid PayrollGenerateDTO dto) {
        return payrollService.generate(dto);
    }

    @PATCH
    @Path("/{id}/adjust")
    public PayrollDTO adjust(@PathParam("id") UUID id, PayrollAdjustDTO dto) {
        return payrollService.adjust(id, dto);
    }

    @PATCH
    @Path("/{id}/mark-paid")
    public PayrollDTO markPaid(@PathParam("id") UUID id) {
        return payrollService.markPaid(id);
    }
}
