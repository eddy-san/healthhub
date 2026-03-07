package de.healthhub.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/app/*", "/admin/*"})
public class AuthFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        String uri = request.getRequestURI();

        if (uri.contains("/index.xhtml") || uri.contains("/jakarta.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && Boolean.TRUE.equals(session.getAttribute("loggedIn"));

        if (!loggedIn) {
            response.sendRedirect(request.getContextPath() + "/index.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}