package com.buildcrew.dashboard;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"owner"})
public class DashboardResource {

    @Inject
    DashboardService dashboardService;

    @GET
    @Path("/summary")
    public DashboardSummaryDTO getSummary() {
        return dashboardService.getSummary();
    }
}
