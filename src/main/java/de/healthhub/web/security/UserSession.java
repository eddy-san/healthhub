package de.healthhub.web.security;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

    private String username;
    private String role;
    private boolean loggedIn;

    public void login(String username, String role) {
        this.username = username;
        this.role = role;
        this.loggedIn = true;
    }

    public void logout() {
        this.username = null;
        this.role = null;
        this.loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}