package de.healthhub.auth.api;

import de.healthhub.auth.service.AuthenticationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
    public Response login() {
        return Response.status(Response.Status.GONE)
                .entity("""
                    {"error":"deprecated","message":"API login moved to Keycloak. Use the OpenID Connect token endpoint."}
                """)
                .build();
    }

    @GET
    @Path("/live")
    public Response live() {
        return Response.ok("""
            {"status":"UP","check":"live"}
        """).build();
    }

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