package de.healthhub.measurement.api;

import de.healthhub.measurement.model.MeasurementCreateRequest;
import de.healthhub.measurement.model.MeasurementMeResponse;
import de.healthhub.measurement.service.MeasurementService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/measurements")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MeasurementResource {

    @Inject
    private MeasurementService measurementService;

    // =========================
    // POST /api/v1/measurements
    // =========================
    @POST
    public Response create(MeasurementCreateRequest request) {

        try {
            measurementService.createMeasurement(request);
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

    // =========================
    // GET /api/v1/measurements/me
    // =========================
    @GET
    @Path("/me")
    public Response me() {
        MeasurementMeResponse response = measurementService.getCurrentPatientView();

        if (response.error() != null) {
            Response.Status status = switch (response.error()) {
                case "No authenticated user" -> Response.Status.UNAUTHORIZED;
                case "PATIENT role required", "No patient assigned to user" -> Response.Status.FORBIDDEN;
                default -> Response.Status.BAD_REQUEST;
            };
            return Response.status(status).entity(response).build();
        }

        return Response.ok(response).build();
    }

    // =========================
    // GET /api/v1/measurements/live
    // =========================
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