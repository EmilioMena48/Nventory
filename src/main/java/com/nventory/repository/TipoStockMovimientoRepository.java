package com.nventory.repository;

import com.nventory.model.TipoStockMovimiento;
import jakarta.persistence.EntityManager;
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
}
