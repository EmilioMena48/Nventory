package com.nventory.repository;

import com.nventory.model.TipoModeloInventario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class TipoModeloInventarioRepository extends BaseRepositoryImpl<TipoModeloInventario, Long> {
    public TipoModeloInventarioRepository() {
        super(TipoModeloInventario.class);
    }

    public boolean existeTipoModeloPorNombre(String nombre) {
        try {
            EntityManager em = IndireccionJPA.getEntityManager();
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e) FROM TipoModeloInventario e WHERE e.nombreModeloInventario = :nombre", Long.class);
            query.setParameter("nombre", nombre);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public Long GuardarYRetornarID(TipoModeloInventario model){
        EntityManager em = IndireccionJPA.getEntityManager();
        em.getTransaction().begin();
        em.persist(model);
        em.getTransaction().commit();
        return model.getCodTipoModeloI();
    }
}
