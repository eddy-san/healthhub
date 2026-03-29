package de.healthhub.auth.repository;

import de.healthhub.auth.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository {

    @PersistenceContext(unitName = "healthhubPU")
    private EntityManager em;

    public User save(User user) {
        em.persist(user);
        return user;
    }

    public User update(User user) {
        return em.merge(user);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByKeycloakSubject(String subject) {
        List<User> result = em.createQuery(
                        "select u from User u where u.keycloakSubject = :subject",
                        User.class)
                .setParameter("subject", subject)
                .getResultList();

        return result.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        List<User> result = em.createQuery(
                        "select u from User u where u.username = :username",
                        User.class)
                .setParameter("username", username)
                .getResultList();

        return result.stream().findFirst();
    }
}