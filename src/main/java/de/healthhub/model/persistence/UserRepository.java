package de.healthhub.model.persistence;

import de.healthhub.model.domain.user.User;
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

    public Optional<User> findByUsername(String username) {
        List<User> result = em.createQuery(
                        "select distinct u from User u left join fetch u.roles where u.username = :username",
                        User.class)
                .setParameter("username", username)
                .getResultList();

        return result.stream().findFirst();
    }

    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                        "select count(u) from User u where u.username = :username",
                        Long.class)
                .setParameter("username", username)
                .getSingleResult();

        return count > 0;
    }
}
