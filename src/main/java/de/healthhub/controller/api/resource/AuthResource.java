package de.healthhub.controller.api.resource;

import de.healthhub.controller.api.dto.LoginRequest;
import de.healthhub.controller.api.dto.LoginResponse;
import de.healthhub.model.service.AuthenticationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {

        try {

            LoginResponse response =
                    authenticationService.loginApi(
                            request.getUsername(),
                            request.getPassword()
                    );

            return Response.ok(response).build();

        } catch (Exception e) {

            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid credentials")
                    .build();
        }
    }
}