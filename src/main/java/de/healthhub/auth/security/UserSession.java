package de.healthhub.auth.security;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private LoggedInUser currentUser;

    public void login(LoggedInUser user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public LoggedInUser getCurrentUser() {
        return currentUser;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
}