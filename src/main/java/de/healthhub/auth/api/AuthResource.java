package de.healthhub.auth.api;

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

    @POST
    @Path("/login")
    public Response login() {
        return Response.status(Response.Status.GONE)
                .entity("""
                    {"error":"Deprecated","message":"Use Keycloak token endpoint instead of /api/auth/login"}
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
        return Response.ok("""
            {"status":"UP","check":"ready"}
        """).build();
    }
}