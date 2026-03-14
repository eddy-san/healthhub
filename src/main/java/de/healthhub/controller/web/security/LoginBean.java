package de.healthhub.controller.web.security;

import de.healthhub.model.service.AuthenticationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;

    @Inject
    private AuthenticationService authenticationService;

    public String login() {
        boolean success = authenticationService.login(username, password);
        if (!success) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login failed",
                            "Invalid username or password"));
            return null;
        }

        return "/app/home.xhtml?faces-redirect=true";
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
