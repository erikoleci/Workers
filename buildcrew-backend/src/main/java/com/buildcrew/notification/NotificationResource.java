package com.buildcrew.notification;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    @GET
    public List<NotificationDTO> list(@QueryParam("unreadOnly") boolean unreadOnly) {
        return notificationService.list(unreadOnly);
    }

    @PATCH
    @Path("/{id}/read")
    public Response markRead(@PathParam("id") UUID id) {
        notificationService.markRead(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/read-all")
    public Response markAllRead() {
        notificationService.markAllRead();
        return Response.noContent().build();
    }
}
