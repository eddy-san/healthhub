package de.healthhub.auth.security;

import de.healthhub.auth.security.ApiRequestUser;
import de.healthhub.auth.security.JwtService;
import de.healthhub.auth.security.LoggedInUser;
import de.healthhub.measurement.model.Patient;
import de.healthhub.auth.model.Role;
import de.healthhub.auth.model.RoleName;
import de.healthhub.auth.model.User;
import de.healthhub.measurement.repository.PatientRepository;
import de.healthhub.auth.repository.UserRepository;
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

        // Login-Endpunkt frei lassen
        // Öffentliche Endpunkte frei lassen
        if (path.equals("auth/login") || path.endsWith("/auth/login")
                || path.equals("auth/live") || path.endsWith("/auth/live")
                || path.equals("auth/ready") || path.endsWith("/auth/ready")
                || path.equals("measurements/live") || path.endsWith("/measurements/live")
                || path.equals("measurements/ready") || path.endsWith("/measurements/ready")
                || path.equals("health/live") || path.endsWith("/health/live")
                || path.equals("health/ready") || path.endsWith("/health/ready")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Missing Authorization header");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Invalid Authorization header");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        if (token.isEmpty()) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Missing bearer token");
            return;
        }

        if (!jwtService.isValid(token)) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Invalid token");
            return;
        }

        String username = jwtService.extractUsername(token);

        if (username == null || username.isBlank()) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Invalid token subject");
            return;
        }

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "User not found");
            return;
        }

        if (!user.isEnabled()) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "User disabled");
            return;
        }

        Set<RoleName> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        if (!roleNames.contains(RoleName.PATIENT)) {
            abort(requestContext, Response.Status.FORBIDDEN, "PATIENT role required");
            return;
        }

        Long patientId = patientRepository.findByUserId(user.getId())
                .map(Patient::getId)
                .orElse(null);

        if (patientId == null) {
            abort(requestContext, Response.Status.FORBIDDEN, "No patient assigned to user");
            return;
        }

        LoggedInUser loggedInUser = new LoggedInUser(
                user.getId(),
                user.getUsername(),
                roleNames,
                patientId
        );

        apiRequestUser.setLoggedInUser(loggedInUser);
    }

    private void abort(ContainerRequestContext requestContext,
                       Response.Status status,
                       String message) {
        requestContext.abortWith(
                Response.status(status)
                        .entity(message)
                        .build()
        );
    }
}