package de.healthhub.auth;

import de.healthhub.domain.auth.RoleName;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private LoggedInUser currentUser;

    public LoggedInUser getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void login(LoggedInUser loggedInUser) {
        this.currentUser = loggedInUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean hasRole(RoleName roleName) {
        return currentUser != null && currentUser.hasRole(roleName);
    }

    public boolean isPatient() {
        return currentUser != null && currentUser.isPatient();
    }

    public boolean isAdmin() {
        return hasRole(RoleName.ADMIN);
    }

    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    public Long getCurrentPatientId() {
        return currentUser != null ? currentUser.getPatientId() : null;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
}
