package de.healthhub.web.security;

import de.healthhub.auth.UserSession;
import de.healthhub.domain.auth.RoleName;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthFilter extends HttpFilter {

    @Inject
    private UserSession userSession;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (uri.endsWith("/index.xhtml") || uri.equals(contextPath + "/") || uri.contains("/jakarta.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

        if (userSession == null || !userSession.isLoggedIn()) {
            response.sendRedirect(contextPath + "/index.xhtml");
            return;
        }

        if (uri.startsWith(contextPath + "/admin/") && !userSession.hasRole(RoleName.ADMIN)) {
            response.sendRedirect(contextPath + "/app/home.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}
