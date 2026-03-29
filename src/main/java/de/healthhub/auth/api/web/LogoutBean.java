package de.healthhub.auth.api.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Named
@RequestScoped
public class LogoutBean {

    public void logout() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        String idToken = (String) request.getAttribute("org.wildfly.security.http.oidc.OidcIdToken");
        String redirectUri = URLEncoder.encode(
                "https://healthhub.roth-it-solutions.de",
                StandardCharsets.UTF_8
        );

        String keycloakLogoutUrl;

        if (idToken != null && !idToken.isBlank()) {
            keycloakLogoutUrl =
                    "https://auth.roth-it-solutions.de/realms/healthhub/protocol/openid-connect/logout" +
                            "?id_token_hint=" + URLEncoder.encode(idToken, StandardCharsets.UTF_8) +
                            "&post_logout_redirect_uri=" + redirectUri;
        } else {
            keycloakLogoutUrl =
                    "https://auth.roth-it-solutions.de/realms/healthhub/protocol/openid-connect/logout" +
                            "?post_logout_redirect_uri=" + redirectUri;
        }

        try {
            request.logout();
        } catch (ServletException e) {
            throw new IOException("Logout failed", e);
        }

        externalContext.invalidateSession();
        externalContext.redirect(keycloakLogoutUrl);
        facesContext.responseComplete();
    }
}