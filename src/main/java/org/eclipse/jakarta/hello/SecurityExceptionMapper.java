package org.eclipse.jakarta.hello;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = Logger.getLogger(SecurityExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.log(Level.WARNING, "Security exception occurred: {0}", exception.getMessage());
        
        if (exception instanceof NotAuthorizedException) {
            return handleNotAuthorized((NotAuthorizedException) exception);
        } else if (exception instanceof ForbiddenException) {
            return handleForbidden((ForbiddenException) exception);
        }
        
        // For other security-related exceptions, check the message or type
        String message = exception.getMessage();
        if (message != null && (message.contains("Access forbidden") || message.contains("role not allowed"))) {
            return handleForbidden(new ForbiddenException(message));
        }
        
        // Not a security exception we handle
        return null;
    }

    private Response handleNotAuthorized(NotAuthorizedException exception) {
        ErrorResponse error = new ErrorResponse(
            "AUTHENTICATION_REQUIRED",
            "Authentication is required to access this resource",
            "Please provide valid credentials using Basic Authentication",
            LocalDateTime.now().toString(),
            401
        );
        
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .header("WWW-Authenticate", "Basic realm=\"user-realm\"")
                .build();
    }

    private Response handleForbidden(ForbiddenException exception) {
        ErrorResponse error = new ErrorResponse(
            "ACCESS_DENIED",
            "You don't have sufficient permissions to access this resource",
            "This endpoint requires the 'user' role. Current user doesn't have the required role.",
            LocalDateTime.now().toString(),
            403
        );
        
        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private String details;
        private String timestamp;
        private int statusCode;

        public ErrorResponse() {}

        public ErrorResponse(String errorCode, String message, String details, String timestamp, int statusCode) {
            this.errorCode = errorCode;
            this.message = message;
            this.details = details;
            this.timestamp = timestamp;
            this.statusCode = statusCode;
        }

        // Getters and setters
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    }
}