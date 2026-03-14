package de.healthhub.controller.web.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;

@Named
@RequestScoped
public class LogoutBean {

    public void logout() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        context.getExternalContext().invalidateSession();
        context.getExternalContext().redirect(
                context.getExternalContext().getRequestContextPath() + "/index.xhtml"
        );
        context.responseComplete();
    }
}