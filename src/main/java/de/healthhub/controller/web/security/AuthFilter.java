package de.healthhub.controller.web.security;

import de.healthhub.infrastructure.UserSession;
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

        boolean loginPage =
                uri.equals(contextPath + "/index.xhtml") ||
                        uri.equals(contextPath + "/");

        boolean publicResource =
                uri.startsWith(contextPath + "/jakarta.faces.resource/");

        boolean loggedIn =
                userSession != null && userSession.isLoggedIn();

        if (publicResource || loginPage) {
            chain.doFilter(request, response);
            return;
        }

        if (!loggedIn) {
            response.sendRedirect(contextPath + "/index.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}