package de.healthhub.persistence;

import de.healthhub.domain.auth.RoleEntity;
import de.healthhub.domain.auth.RoleName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleRepository {

    @PersistenceContext(unitName = "healthhubPU")
    private EntityManager em;

    public RoleEntity save(RoleEntity role) {
        em.persist(role);
        return role;
    }

    public Optional<RoleEntity> findByRoleName(RoleName roleName) {
        List<RoleEntity> result = em.createQuery(
                        "select r from RoleEntity r where r.roleName = :roleName",
                        RoleEntity.class)
                .setParameter("roleName", roleName)
                .getResultList();

        return result.stream().findFirst();
    }
}
