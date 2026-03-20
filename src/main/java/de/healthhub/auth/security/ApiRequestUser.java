package de.healthhub.auth.security;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ApiRequestUser {

    private LoggedInUser loggedInUser;

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public boolean isAuthenticated() {
        return loggedInUser != null;
    }
}