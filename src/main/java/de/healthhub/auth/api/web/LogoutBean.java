package de.healthhub.auth.api.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@Named
@RequestScoped
public class LogoutBean {

    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        try {
            request.logout();
        } catch (ServletException e) {
            throw new IOException("Logout failed", e);
        }

        externalContext.invalidateSession();
        externalContext.redirect(externalContext.getRequestContextPath() + "/admin/login.xhtml");
        facesContext.responseComplete();
    }
}