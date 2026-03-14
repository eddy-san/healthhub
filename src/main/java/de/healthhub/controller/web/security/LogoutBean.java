package de.healthhub.controller.web.security;

import de.healthhub.model.service.AuthenticationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LogoutBean {

    @Inject
    private AuthenticationService authenticationService;

    public String logout() {
        authenticationService.logout();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }
}
