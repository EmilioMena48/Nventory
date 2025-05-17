package com.nventory.repository;

import jakarta.persistence.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public abstract class SoftDeletableRepositoryImpl<T, ID> extends BaseRepositoryImpl<T, ID> {

    public SoftDeletableRepositoryImpl(Class<T> entityClass) {
        super(entityClass);
    }

    public void borrar(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                try {
                    // Intenta invocar el método setFechaHoraBajaNombreEntidad(LocalDateTime)
                    String methodName = "setFechaHoraBaja" + entityClass.getSimpleName();
                    Method setFechaHoraBaja = entityClass.getMethod(methodName, LocalDateTime.class);
                    setFechaHoraBaja.invoke(entity, LocalDateTime.now());
                    em.merge(entity);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
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
