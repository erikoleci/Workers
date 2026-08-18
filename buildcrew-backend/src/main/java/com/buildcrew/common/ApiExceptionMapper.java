package com.buildcrew.common;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Without this, a thrown WebApplicationException (BadRequestException,
 * ForbiddenException, NotFoundException, ...) with just a message string
 * doesn't reliably produce a JSON body the frontend can read - the message
 * was getting silently dropped. This wraps it into {"message": "..."}
 * consistently, matching what the frontend already expects everywhere
 * (e.response?.data?.message).
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        String message = exception.getMessage() != null ? exception.getMessage() : "Request failed";

        return Response.status(status)
                .entity(new ErrorBody(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public record ErrorBody(String message) {}
}
