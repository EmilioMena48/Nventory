package com.nventory.repository;

import jakarta.persistence.EntityManager;

public abstract class HardDeletableRepositoryImpl<T, ID> extends BaseRepositoryImpl<T, ID> {

    public HardDeletableRepositoryImpl(Class<T> entityClass) {
        super(entityClass);
    }

    public void borrar(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
