package de.healthhub.model.persistence;

import de.healthhub.model.domain.user.Role;
import de.healthhub.model.domain.user.RoleName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleRepository {

    @PersistenceContext(unitName = "healthhubPU")
    private EntityManager em;

    public Role save(Role role) {
        em.persist(role);
        return role;
    }

    public Optional<Role> findByRoleName(RoleName roleName) {
        List<Role> result = em.createQuery(
                        "select r from Role r where r.roleName = :roleName",
                        Role.class)
                .setParameter("roleName", roleName)
                .getResultList();

        return result.stream().findFirst();
    }
}
