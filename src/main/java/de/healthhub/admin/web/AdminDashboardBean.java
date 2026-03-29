package de.healthhub.admin.web;

import de.healthhub.auth.model.User;
import de.healthhub.auth.service.OidcWebIdentityService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.faces.context.FacesContext;

@RequestScoped
public class AdminDashboardBean {

    @Inject
    OidcWebIdentityService identityService;

    private User currentUser;

    @PostConstruct
    public void init() {

        HttpServletRequest request =
                (HttpServletRequest) FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getRequest();

        currentUser = identityService.getOrCreateFromRequest(request);

        System.out.println("HealthHub admin dashboard currentUser=" + currentUser.getUsername());
    }

    public User getCurrentUser() {
        return currentUser;
    }
}