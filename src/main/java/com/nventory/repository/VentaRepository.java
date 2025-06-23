package com.nventory.repository;

import com.nventory.model.Venta;
import jakarta.persistence.EntityManager;

public class VentaRepository extends BaseRepositoryImpl <Venta, Long> {
    public VentaRepository() {
        super(Venta.class);
    }

    public Venta guardarNuevaVenta(Venta entity) {
        EntityManager em = getEntityManager();
        Venta ventaPersistida = null;
        try {
            em.getTransaction().begin();

            if (entity.getNumeroVenta() == null) {
                em.persist(entity);
                em.flush();
                ventaPersistida = entity;
            } else {
                ventaPersistida = em.merge(entity);
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
        return ventaPersistida;
    }

    public Venta buscarVentaConArticulos(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT v FROM Venta v LEFT JOIN FETCH v.ventaArticulo WHERE v.numeroVenta = :id", Venta.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }



}
