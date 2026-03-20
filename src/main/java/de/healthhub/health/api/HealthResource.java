package de.healthhub.health.api;

import de.healthhub.health.service.HealthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    @Inject
    private HealthService healthService;

    @GET
    @Path("/live")
    public Response live() {
        return Response.ok(new HealthStatusResponse("UP", "live")).build();
    }

    @GET
    @Path("/ready")
    public Response ready() {
        if (healthService.isReady()) {
            return Response.ok(new HealthStatusResponse("UP", "ready")).build();
        }

        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new HealthStatusResponse("DOWN", "ready"))
                .build();
    }

    public record HealthStatusResponse(String status, String check) {}
}
