package de.healthhub.bootstrap;

import de.healthhub.auth.api.AuthResource;
import de.healthhub.health.api.HealthResource;
import de.healthhub.measurement.api.MeasurementResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class HealthHubApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(HealthResource.class);
        classes.add(AuthResource.class);
        classes.add(MeasurementResource.class);

        return classes;
    }
}