package com.buildcrew.dashboard;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class DashboardResource {

    @Inject
    DashboardService dashboardService;

    @GET
    @Path("/summary")
    public DashboardSummaryDTO getSummary() {
        return dashboardService.getSummary();
    }
}
