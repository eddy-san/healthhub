package de.healthhub.controller.web.app;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;

@Named
@RequestScoped
public class NavBean {

    public void redirectAdmin() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(ec.getRequestContextPath() + "/admin/dashboard.xhtml");
    }
}