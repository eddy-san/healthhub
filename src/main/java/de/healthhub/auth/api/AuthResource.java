package de.healthhub.auth.api;

import de.healthhub.auth.api.LoginRequest;
import de.healthhub.auth.api.LoginResponse;
import de.healthhub.auth.service.AuthenticationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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

    // ------------------------
    // LIVE
    // ------------------------
    @GET
    @Path("/live")
    public Response live() {
        return Response.ok("""
            {"status":"UP","check":"live"}
        """).build();
    }

    // ------------------------
    // READY
    // ------------------------
    @GET
    @Path("/ready")
    public Response ready() {

        try {
            boolean ready = authenticationService.isReady();

            if (ready) {
                return Response.ok("""
                    {"status":"UP","check":"ready"}
                """).build();
            }

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("""
                        {"status":"DOWN","check":"ready"}
                    """)
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("""
                        {"status":"DOWN","check":"ready"}
                    """)
                    .build();
        }
    }
}