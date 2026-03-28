package de.healthhub.measurement.api;

import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.service.MeasurementService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/v1/measurements")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MeasurementResource {

    @Inject
    private MeasurementService measurementService;

    @POST
    @RolesAllowed("PATIENT")
    public Response create(MeasurementCreateRequest request, @Context SecurityContext securityContext) {
        try {
            String username = measurementService.extractUsername(securityContext);
            measurementService.createMeasurement(username, request);
            return Response.status(Response.Status.CREATED).build();

        } catch (IllegalStateException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create measurement")
                    .build();
        }
    }

    @GET
    @Path("/me")
    @RolesAllowed("PATIENT")
    public Response me(@Context SecurityContext securityContext) {
        try {
            String username = measurementService.extractUsername(securityContext);
            MeasurementMeResponse response = measurementService.getCurrentPatientView(username);
            return Response.ok(response).build();

        } catch (IllegalStateException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(MeasurementMeResponse.error(e.getMessage()))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(MeasurementMeResponse.error(e.getMessage()))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(MeasurementMeResponse.error("Failed to resolve current patient"))
                    .build();
        }
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
            return Response.ok("""
                {"status":"UP","check":"ready"}
            """).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("""
                        {"status":"DOWN","check":"ready"}
                    """)
                    .build();
        }
    }
}