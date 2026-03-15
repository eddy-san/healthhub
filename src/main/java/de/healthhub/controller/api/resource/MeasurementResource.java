package de.healthhub.controller.api.resource;

import de.healthhub.infrastructure.ApiRequestUser;
import de.healthhub.infrastructure.LoggedInUser;
import de.healthhub.model.domain.user.RoleName;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/measurements")
@Produces(MediaType.APPLICATION_JSON)
public class MeasurementResource {

    @Inject
    private ApiRequestUser apiRequestUser;

    @GET
    @Path("/me")
    public Response me() {

        LoggedInUser user = apiRequestUser.getLoggedInUser();

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new MeResponse("No authenticated user", null, null, null))
                    .build();
        }

        if (!user.getRoles().contains(RoleName.PATIENT)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new MeResponse("PATIENT role required", null, null, null))
                    .build();
        }

        MeResponse response = new MeResponse(
                user.getUsername(),
                user.getUserId(),
                user.getPatientId(),
                user.getRoles().stream().map(Enum::name).toList()
        );

        return Response.ok(response).build();
    }

    public static class MeResponse {
        private String username;
        private Long userId;
        private Long patientId;
        private java.util.List<String> roles;

        public MeResponse() {
        }

        public MeResponse(String username, Long userId, Long patientId, java.util.List<String> roles) {
            this.username = username;
            this.userId = userId;
            this.patientId = patientId;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getPatientId() {
            return patientId;
        }

        public void setPatientId(Long patientId) {
            this.patientId = patientId;
        }

        public java.util.List<String> getRoles() {
            return roles;
        }

        public void setRoles(java.util.List<String> roles) {
            this.roles = roles;
        }
    }
}