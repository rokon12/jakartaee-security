package org.eclipse.jakarta.hello;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("hello")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class HelloWorldResource {
    private static final Logger LOGGER = Logger.getLogger(HelloWorldResource.class.getName());

    @GET
    @RolesAllowed("user")
    public Response hello(@Context SecurityContext securityContext, @QueryParam("name") String name) {
        String target = sanitizeName(name);
        String currentUser = getCurrentUser(securityContext);
        
        String message = String.format(
                "Hello, %s! You are authenticated as: %s. You have the 'user' role.",
                target, currentUser);

        return createSuccessResponse(message);
    }

    @GET
    @Path("admin")
    @RolesAllowed("admin")
    public Response admin(@Context SecurityContext securityContext) {
        String currentUser = getCurrentUser(securityContext);
        
        String message = String.format(
                "Admin endpoint accessed by: %s (with admin role)",
                currentUser);

        return createSuccessResponse(message);
    }

    @GET
    @Path("public")
    public Response publicEndpoint() {
        return createSuccessResponse("This is a public endpoint – no authentication required");
    }

    @GET
    @Path("guest")
    @RolesAllowed("guest")
    public Response guest(@Context SecurityContext securityContext) {
        String currentUser = getCurrentUser(securityContext);
        
        String message = String.format(
                "Guest endpoint accessed by: %s (with guest role)",
                currentUser);
        
        return createSuccessResponse(message);
    }

    private String getCurrentUser(SecurityContext securityContext) {
        if (securityContext == null) {
            LOGGER.log(Level.WARNING, "SecurityContext is null");
            return "unknown";
        }
        
        Principal principal = securityContext.getUserPrincipal();
        if (principal != null) {
            return principal.getName();
        } else {
            LOGGER.log(Level.WARNING, "User Principal is null after authentication - this should not happen");
            return "unknown";
        }
    }
    
    private String sanitizeName(String name) {
        return (name == null || name.isBlank()) ? "world" : name.trim();
    }
    
    private Response createSuccessResponse(String message) {
        return Response.ok(new Hello(message)).build();
    }
}