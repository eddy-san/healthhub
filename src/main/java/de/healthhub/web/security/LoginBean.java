package de.healthhub.web.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;

    public String login() {
        String expectedUser = System.getenv("APP_LOGIN_USER");
        String expectedPassword = System.getenv("APP_LOGIN_PASSWORD");

        if (expectedUser == null || expectedPassword == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Configuration error",
                            "APP_LOGIN_USER or APP_LOGIN_PASSWORD not set"));
            return null;
        }

        if (expectedUser.equals(username) && expectedPassword.equals(password)) {
            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .put("loggedIn", true);

            FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getSessionMap()
                    .put("username", username);

            return "/app/home.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Login failed",
                        "Invalid username or password"));

        return null;
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}