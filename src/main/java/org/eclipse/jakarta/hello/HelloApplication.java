package org.eclipse.jakarta.hello;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ApplicationPath;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.annotation.security.DeclareRoles;

@BasicAuthenticationMechanismDefinition(realmName = "user-realm")
@DeclareRoles({"user", "admin", "guest"})
@ApplicationPath("rest")
@ApplicationScoped
public class HelloApplication extends Application {
}