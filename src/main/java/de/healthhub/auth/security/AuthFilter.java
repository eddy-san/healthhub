package de.healthhub.auth.security;

import de.healthhub.auth.security.LoggedInUser;
import de.healthhub.auth.security.UserSession;
import de.healthhub.auth.model.RoleName;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("*.xhtml")
public class AuthFilter extends HttpFilter {

    @Inject
    private UserSession userSession;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();

        boolean landingPage =
                uri.equals(contextPath + "/") ||
                        uri.equals(contextPath + "/index.xhtml");

        boolean adminLoginPage =
                uri.equals(contextPath + "/admin/login.xhtml");

        boolean publicResource =
                uri.startsWith(contextPath + "/jakarta.faces.resource/");

        boolean adminArea =
                uri.startsWith(contextPath + "/admin/");

        LoggedInUser currentUser =
                userSession != null ? userSession.getCurrentUser() : null;

        boolean loggedIn =
                currentUser != null;

        boolean isAdmin =
                loggedIn
                        && currentUser.getRoles() != null
                        && currentUser.getRoles().contains(RoleName.ADMIN);

        if (publicResource || landingPage || adminLoginPage) {
            chain.doFilter(request, response);
            return;
        }

        if (adminArea) {
            if (!loggedIn) {
                response.sendRedirect(contextPath + "/admin/login.xhtml");
                return;
            }

            if (!isAdmin) {
                userSession.logout();
                response.sendRedirect(contextPath + "/admin/login.xhtml");
                return;
            }
        }

        if (!loggedIn) {
            response.sendRedirect(contextPath + "/admin/login.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}