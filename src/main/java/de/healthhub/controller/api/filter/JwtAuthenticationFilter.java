package de.healthhub.controller.api.filter;

import de.healthhub.infrastructure.ApiRequestUser;
import de.healthhub.infrastructure.JwtService;
import de.healthhub.infrastructure.LoggedInUser;
import de.healthhub.model.domain.patient.Patient;
import de.healthhub.model.domain.user.Role;
import de.healthhub.model.domain.user.RoleName;
import de.healthhub.model.domain.user.User;
import de.healthhub.model.persistence.PatientRepository;
import de.healthhub.model.persistence.UserRepository;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private JwtService jwtService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PatientRepository patientRepository;

    @Inject
    private ApiRequestUser apiRequestUser;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();

        // Login offen lassen
        if (path.equals("auth/login") || path.endsWith("/auth/login")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Missing Authorization header")
                            .build()
            );
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (!jwtService.isValid(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Invalid token")
                            .build()
            );
            return;
        }

        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !user.isEnabled()) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Unknown or disabled user")
                            .build()
            );
            return;
        }

        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        Long patientId = patientRepository.findByUserId(user.getId())
                .map(Patient::getId)
                .orElse(null);

        LoggedInUser loggedInUser =
                new LoggedInUser(user.getId(), user.getUsername(), roleNames, patientId);

        apiRequestUser.setLoggedInUser(loggedInUser);
    }
}