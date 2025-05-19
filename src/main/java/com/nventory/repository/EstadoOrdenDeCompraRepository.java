package com.nventory.repository;

import com.nventory.model.EstadoOrdenDeCompra;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class EstadoOrdenDeCompraRepository extends SoftDeletableRepositoryImpl<EstadoOrdenDeCompra, Long> {
    public EstadoOrdenDeCompraRepository() {
        super(EstadoOrdenDeCompra.class);
    }

    public boolean existeEstadoPorNombre(String nombreEstado){
        EntityManager em = IndireccionJPA.getEntityManager();
        try {
            TypedQuery<Long> query =  em.createQuery("SELECT COUNT(e) FROM EstadoOrdenDeCompra e WHERE e.nombreEstadoOC = :nombreEstado", Long.class);
            query.setParameter("nombreEstado", nombreEstado);
            Long count = query.getSingleResult();
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }

    }
}
