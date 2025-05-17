package com.nventory.repository;

import jakarta.persistence.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {

    protected final Class<T> entityClass;

    public BaseRepositoryImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return IndireccionJPA.getEntityManager();
    }

    @Override
    public List<T> buscarTodos() {
        EntityManager em = getEntityManager();
        try {
            String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(query, entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public T buscarPorId(ID id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void guardar(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
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
