package org.eclipse.jakarta.hello;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("hello")
public class HelloWorldResource {
    private static final Logger LOGGER = Logger.getLogger(HelloWorldResource.class.getName());

    @GET
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    public Response hello(@Context SecurityContext securityContext, @QueryParam("name") String name) {
        String userNameToGreet = (name == null) || name.trim().isEmpty() ? "world" : name;

        Principal userPrincipal = securityContext.getUserPrincipal();
        String authenticatedUsername = "anonymous";

        if (userPrincipal != null) {
            authenticatedUsername = userPrincipal.getName();
            LOGGER.log(Level.INFO, "Successful access to hello endpoint - User: {0}, Roles: user", authenticatedUsername);
            
            // Log additional security context info
            boolean hasUserRole = securityContext.isUserInRole("user");
            boolean hasAdminRole = securityContext.isUserInRole("admin");
            LOGGER.log(Level.INFO, "User {0} - hasUserRole: {1}, hasAdminRole: {2}", 
                      new Object[]{authenticatedUsername, hasUserRole, hasAdminRole});
        } else {
            LOGGER.log(Level.WARNING, "User Principal is NULL after @RolesAllowed - this should not happen");
        }

        String message = String.format("Hello, %s! You are authenticated as: %s. You have the 'user' role.", 
                                      userNameToGreet, authenticatedUsername);
        return Response.ok(new Hello(message)).build();
    }

    @GET
    @Path("admin")
    @RolesAllowed("admin")
    @Produces({MediaType.APPLICATION_JSON})
    public Response adminEndpoint(@Context SecurityContext securityContext) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        String authenticatedUsername = userPrincipal != null ? userPrincipal.getName() : "unknown";
        
        LOGGER.log(Level.INFO, "Successful access to admin endpoint - User: {0}, Roles: admin", authenticatedUsername);
        
        boolean hasAdminRole = securityContext.isUserInRole("admin");
        boolean hasUserRole = securityContext.isUserInRole("user");
        LOGGER.log(Level.INFO, "Admin user {0} - hasAdminRole: {1}, hasUserRole: {2}", 
                  new Object[]{authenticatedUsername, hasAdminRole, hasUserRole});

        String message = String.format("Admin endpoint accessed by: %s (with admin role)", authenticatedUsername);
        return Response.ok(new Hello(message)).build();
    }

    @GET
    @Path("public")
    @Produces({MediaType.APPLICATION_JSON})
    public Response publicEndpoint() {
        LOGGER.log(Level.INFO, "Public endpoint accessed - no authentication required");
        String message = "This is a public endpoint - no authentication required";
        return Response.ok(new Hello(message)).build();
    }

    @GET
    @Path("guest")
    @RolesAllowed("guest")
    @Produces({MediaType.APPLICATION_JSON})
    public Response guestEndpoint(@Context SecurityContext securityContext) {
        Principal userPrincipal = securityContext.getUserPrincipal();
        String authenticatedUsername = userPrincipal != null ? userPrincipal.getName() : "unknown";
        
        LOGGER.log(Level.INFO, "Successful access to guest endpoint - User: {0}, Roles: guest", authenticatedUsername);

        String message = String.format("Guest endpoint accessed by: %s (with guest role)", authenticatedUsername);
        return Response.ok(new Hello(message)).build();
    }
}