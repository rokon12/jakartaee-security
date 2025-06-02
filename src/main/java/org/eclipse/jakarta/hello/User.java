package org.eclipse.jakarta.hello;

import java.util.Set;

public record User(String username, String password, Set<String> roles) {
}
