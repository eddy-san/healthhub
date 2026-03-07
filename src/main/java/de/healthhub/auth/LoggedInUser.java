package de.healthhub.auth;

import de.healthhub.domain.auth.RoleName;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LoggedInUser implements Serializable {

    private final Long userId;
    private final String username;
    private final Set<RoleName> roles;
    private final Long patientId;

    public LoggedInUser(Long userId, String username, Set<RoleName> roles, Long patientId) {
        this.userId = userId;
        this.username = username;
        this.roles = roles == null ? Collections.emptySet() : new HashSet<>(roles);
        this.patientId = patientId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Set<RoleName> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Long getPatientId() {
        return patientId;
    }

    public boolean hasRole(RoleName roleName) {
        return roles.contains(roleName);
    }

    public boolean isPatient() {
        return patientId != null;
    }
}
