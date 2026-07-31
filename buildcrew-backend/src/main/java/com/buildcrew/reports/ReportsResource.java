package com.buildcrew.reports;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.List;

@Path("/api/reports")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RolesAllowed({"owner", "manager"})
public class ReportsResource {

    @Inject
    ReportsService reportsService;

    @GET
    @Path("/production/by-worker")
    public List<ReportDTOs.ProductionByWorker> byWorker(
            @QueryParam("from") LocalDate from,
            @QueryParam("to") LocalDate to) {
        return reportsService.productionByWorker(from, to);
    }

    @GET
    @Path("/production/by-crew")
    public List<ReportDTOs.ProductionByCrew> byCrew(
            @QueryParam("from") LocalDate from,
            @QueryParam("to") LocalDate to) {
        return reportsService.productionByCrew(from, to);
    }

    @GET
    @Path("/production/by-project")
    public List<ReportDTOs.ProductionByProject> byProject() {
        return reportsService.productionByProject();
    }

    @GET
    @Path("/production/monthly")
    public List<ReportDTOs.MonthlyProduction> monthly(@QueryParam("months") @DefaultValue("6") int months) {
        return reportsService.monthlyProduction(months);
    }

    @GET
    @Path("/financial-summary")
    public ReportDTOs.FinancialSummary financialSummary(
            @QueryParam("from") LocalDate from,
            @QueryParam("to") LocalDate to) {
        return reportsService.financialSummary(from, to);
    }
}
