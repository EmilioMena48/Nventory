package com.nventory.repository;

import com.nventory.model.EstadoOrdenDeCompra;
import com.nventory.model.TipoStockMovimiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class TipoStockMovimientoRepository extends SoftDeletableRepositoryImpl<TipoStockMovimiento, Long> {
    public TipoStockMovimientoRepository() {
        super(TipoStockMovimiento.class);
    }

    public boolean existeTipoStockPorNombre(String nombre) {
        EntityManager em = IndireccionJPA.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM TipoStockMovimiento e WHERE e.nombreTipoStockMovimiento = :nombre", Long.class);
            query.setParameter("nombre", nombre);
            Long count = query.getSingleResult();
            return count!= null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public TipoStockMovimiento buscarTSMPorNombre(String nombreMovimiento) {
        EntityManager em = IndireccionJPA.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t FROM TipoStockMovimiento t WHERE t.nombreTipoStockMovimiento= :nombre", TipoStockMovimiento.class)
                    .setParameter("nombre", nombreMovimiento)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
