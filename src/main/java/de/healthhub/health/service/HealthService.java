package de.healthhub.health.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class HealthService {

    @PersistenceContext(unitName = "healthhubPU")
    private EntityManager em;

    public boolean isReady() {
        try {
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }
}
