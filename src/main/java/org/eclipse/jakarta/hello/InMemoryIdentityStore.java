package org.eclipse.jakarta.hello;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Priority(70)
public class InMemoryIdentityStore implements IdentityStore {
    private static final Logger LOGGER = Logger.getLogger(InMemoryIdentityStore.class.getName());

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("InMemoryIdentityStore @PostConstruct init() called");
        // Initialize with some test users
        users.put("user", new User("user", "password", Set.of("user")));
        users.put("admin", new User("admin", "admin123", Set.of("admin", "user")));
        users.put("guest", new User("guest", "guest", Set.of("guest")));
        LOGGER.info("InMemoryIdentityStore initialized with users: " + users.keySet());
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        LOGGER.info("==== InMemoryIdentityStore.validate() CALLED ====");

        if (credential instanceof UsernamePasswordCredential usernamePassword) {
            String username = usernamePassword.getCaller();
            String password = usernamePassword.getPasswordAsString();

            LOGGER.log(Level.INFO, "Validating username: {0}", username);

            User user = users.get(username);
            if (user != null && user.password().equals(password)) {
                LOGGER.log(Level.INFO, "Authentication SUCCESS for user: {0} with roles: {1}",
                        new Object[]{username, user.roles()});

                return new CredentialValidationResult(username, user.roles());
            } else {
                LOGGER.log(Level.WARNING, "Authentication FAILED for user: {0}", username);
            }
        }

        LOGGER.warning("Returning INVALID_RESULT");
        return CredentialValidationResult.INVALID_RESULT;
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        LOGGER.log(Level.INFO, "getCallerGroups called for: {0}", validationResult.getCallerPrincipal().getName());
        String name = validationResult.getCallerPrincipal().getName();
        User user = users.get(name);
        Set<String> groups = user != null ? user.roles() : Set.of();
        LOGGER.log(Level.INFO, "Returning groups: {0}", groups);
        return groups;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Set.of(ValidationType.VALIDATE, ValidationType.PROVIDE_GROUPS);
    }
}