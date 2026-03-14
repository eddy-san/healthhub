package de.healthhub.model.persistence;

import de.healthhub.model.domain.patient.Patient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientRepository {

    @PersistenceContext(unitName = "healthhubPU")
    private EntityManager em;

    public Patient save(Patient patient) {
        em.persist(patient);
        return patient;
    }

    public Patient update(Patient patient) {
        return em.merge(patient);
    }

    public Optional<Patient> findById(Long id) {
        return Optional.ofNullable(em.find(Patient.class, id));
    }

    public Optional<Patient> findByPatientNumber(String patientNumber) {
        List<Patient> result = em.createQuery(
                        "select p from Patient p join fetch p.user u left join fetch u.roles where p.patientNumber = :patientNumber",
                        Patient.class)
                .setParameter("patientNumber", patientNumber)
                .getResultList();

        return result.stream().findFirst();
    }

    public Optional<Patient> findByUserId(Long userId) {
        List<Patient> result = em.createQuery(
                        "select p from Patient p where p.user.id = :userId",
                        Patient.class)
                .setParameter("userId", userId)
                .getResultList();

        return result.stream().findFirst();
    }
}
