package de.healthhub.admin.web;

import de.healthhub.auth.model.User;
import de.healthhub.auth.service.OidcWebIdentityService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.faces.context.FacesContext;

@Named
@RequestScoped
public class AdminDashboardBean {

    @Inject
    OidcWebIdentityService oidcWebIdentityService;

    private User currentUser;

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequest();

        currentUser = oidcWebIdentityService.getOrCreateFromRequest(request);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}