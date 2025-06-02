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
        String authenticatedUsername = "anonymous"; // Default if principal is null

        if (userPrincipal != null) {
            authenticatedUsername = userPrincipal.getName();
            // Log the Principal object itself and its name
            LOGGER.log(Level.INFO, "In hello() - User Principal: {0}, Name: {1}", new Object[]{userPrincipal, authenticatedUsername});
        } else {
            // This would be unexpected if @RolesAllowed("user") is working
            LOGGER.log(Level.WARNING, "In hello() - User Principal is NULL even after @RolesAllowed(\"user\")");
        }

        String message = String.format("Hello, %s! You are authenticated as: %s. You have the 'user' role.", userNameToGreet, authenticatedUsername);
        return Response.ok(new Hello(message)).build();
    }

    @GET
    @Path("admin")
    @RolesAllowed("admin")
    @Produces({MediaType.APPLICATION_JSON})
    public Response adminEndpoint(@Context SecurityContext securityContext) {
        LOGGER.log(Level.INFO, "Entering adminEndpoint()...");

        String message = "Admin endpoint accessed by: Programmatic 'admin' role check";
        return Response.ok(new Hello(message))
                .build();
    }
}